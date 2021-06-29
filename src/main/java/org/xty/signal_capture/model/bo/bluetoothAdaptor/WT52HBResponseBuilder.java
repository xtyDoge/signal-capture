package org.xty.signal_capture.model.bo.bluetoothAdaptor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
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

    private static final Pattern linkRegex = Pattern.compile("(WIT-LINK)-#(\\d+):\"(.*?)\".*?(0x[A-Z0-9]+)");
    private static final Pattern listRegex = Pattern.compile("(WIT-LIST)-#(\\d+):\"(.*?)\"(0x[A-Z0-9]+)(-[0-9]+)");
    private static final Pattern revRegex = Pattern.compile("(WIT-REV)-#(\\d+):\"(.*?)\"(0x[A-Z0-9]+),([0-9]+),(.*?)");
    private static final Pattern conlistRegex = Pattern.compile("(WIT-CONLIST)-#(\\d+):\"(.*?)\"(0x[A-Z0-9]+),([0-9]+),(.*?)");


    private static final Map<String, BiFunction<String, byte[], CommonResponse>> supplierMap =
            new HashMap<String, BiFunction<String, byte[], CommonResponse>>(){{
        put("WIT-LINK", WT52HBResponseBuilder::parseLinkResponse);
        put("WIT-LIST", WT52HBResponseBuilder::parseListResponse);
        put("WIT-REV", WT52HBResponseBuilder::parseRevResponse);
        put("WIT-CONLIST", WT52HBResponseBuilder::parseRevResponse);
    }};


    public static CommonResponse buildFromTextLine(String textLine, byte[] rawData) {

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

        // 如果map里没注册，就返回Null
        if (!supplierMap.containsKey(header)) {
            return null;
        }

        return supplierMap.get(header).apply(textTrimmed, rawData);
    }

    private static LinkResponse parseLinkResponse(String line, byte[] rawData) {

        Matcher matcher = linkRegex.matcher(line);
        if (!matcher.find()) {
            log.error(String.format("Parse Bluetooth link response [%s] error.", line));
            return null;
        }

        LinkResponse linkResponse = new LinkResponse();
        linkResponse.setHeader(matcher.group(1));
        linkResponse.setDeviceNum(Integer.valueOf(matcher.group(2)));
        linkResponse.setDeviceName(matcher.group(3));
        linkResponse.setDeviceAddress(matcher.group(4));
        return linkResponse;
    }

    private static ListResponse parseListResponse(String line, byte[] rawData) {

        Matcher matcher = listRegex.matcher(line);
        if (!matcher.find()) {
            log.error(String.format("Parse Bluetooth list response [%s] error.", line));
            return null;
        }

        ListResponse listResponse = new ListResponse();
        listResponse.setHeader(matcher.group(1));
        listResponse.setDeviceNum(Integer.valueOf(matcher.group(2)));
        listResponse.setDeviceName(matcher.group(3));
        listResponse.setDeviceAddress(matcher.group(4));
        listResponse.setSignalIntensity(Integer.valueOf(matcher.group(5)));
        return listResponse;
    }

    private static RevResponse parseRevResponse(String line, byte[] rawData) {
        Matcher matcher = revRegex.matcher(line);
        if (!matcher.find() || matcher.groupCount() < 4) {
            log.error(String.format("Parse Bluetooth link response [%s] error.", line));
            return null;
        }

        RevResponse revResponse = new RevResponse();
        revResponse.setHeader(matcher.group(1));
        revResponse.setDeviceNum(Integer.valueOf(matcher.group(2)));
        revResponse.setDeviceName(matcher.group(3));
        revResponse.setDeviceAddress(matcher.group(4));
        revResponse.setContentLength(Integer.valueOf(matcher.group(5)));

        // 如果是传感器数据的话，需要把开头为 0x55 0x61 之后的数据包找到
        for (int i=0; i<rawData.length - 1; i++) {
            if (rawData[i] == (byte) 0x55 && rawData[i+1] == (byte) 0x61) {
                // i 之后contentLength个byte都是数据
                revResponse.setContent(Arrays.copyOfRange(rawData, i, i + revResponse.getContentLength()));
                break;
            }
        }

        return revResponse;
    }


}
