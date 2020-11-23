package model.bo.bluetoothAdaptor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-24
 */
@Slf4j
public class WT52HBResponseBuilder {

    private static final Pattern headerRegex = Pattern.compile("(WIT-[\\D]+)-.*?");

    private static final Pattern linkRegex = Pattern.compile("(WIT-[\\D]+)-#(\\d+):\"(.*?)\".*?(0x[A-Z0-9]+)");
    private static final Pattern listRegex = Pattern.compile("(WIT-LIST)-#(\\d+):\"(.*?)\"(0x[A-Z0-9]+)(-[0-9]+)");
    private static final Pattern revRegex = Pattern.compile("(WIT-[\\D]+)-#(\\d+):\"(.*?)\".*?(0x[A-Z0-9]+),([0-9]+),(.*?)");


    private static final Map<String, Function<String, Object>> supplierMap = new HashMap<String, Function<String, Object>>(){{
        put("WIT-LINK", WT52HBResponseBuilder::parseLinkResponse);
        put("WIT-LIST", WT52HBResponseBuilder::parseListResponse);
        put("WIT-REV", WT52HBResponseBuilder::parseRevResponse);
    }};


    public static Object buildFromTextLine(String textLine) {

        String textTrimmed = textLine.replaceAll(" ", "");
        Matcher headerMatcher = headerRegex.matcher(textTrimmed);
        String header = "";

        try {
            if (!headerMatcher.find()) {
                log.info("[{}], not WIT response, length : {}", textTrimmed, textTrimmed.length());
                return null;
            }
            header = headerMatcher.group(1);
        } catch (IllegalStateException e) {
            log.error("", e);
            log.info("[{}], not WIT response, length : {}", textTrimmed, textTrimmed.length());
            return null;
        }

        return supplierMap.get(header).apply(textTrimmed);
    }

    private static LinkResponse parseLinkResponse(String line) {

        Matcher matcher = linkRegex.matcher(line);
        if (!matcher.find()) {
            log.error(String.format("Parse Bluetooth link response [%s] error.", line));
            return null;
        }

        return LinkResponse.builder()
                .header(matcher.group(1))
                .deviceNum(Integer.valueOf(matcher.group(2)))
                .deviceName(matcher.group(3))
                .deviceAddress(matcher.group(4))
                .build();
    }

    private static ListResponse parseListResponse(String line) {

        Matcher matcher = listRegex.matcher(line);
        if (!matcher.find()) {
            log.error(String.format("Parse Bluetooth list response [%s] error.", line));
            return null;
        }

        return ListResponse.builder()
                .header(matcher.group(1))
                .deviceNum(Integer.valueOf(matcher.group(2)))
                .deviceName(matcher.group(3))
                .deviceAddress(matcher.group(4))
                .signalIntensity(Integer.valueOf(matcher.group(5)))
                .build();
    }

    private static RevResponse parseRevResponse(String line) {
        Matcher matcher = revRegex.matcher(line);
        if (!matcher.find() || matcher.groupCount() < 4) {
            log.error(String.format("Parse Bluetooth link response [%s] error.", line));
            return null;
        }

        return RevResponse.builder()
                .header(matcher.group(1))
                .deviceNum(Integer.valueOf(matcher.group(2)))
                .deviceName(matcher.group(3))
                .deviceAddress(matcher.group(4))
                .contentLength(Integer.valueOf(matcher.group(5)))
                .content(matcher.group(6))
                .build();
    }


}
