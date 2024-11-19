package com.v7idea.healthkit.Domain;

public class FirmwareData {
    //版本大於等於,出現82,84才可以下MP13做停止傳送,小於送MEASURE MODE END
    public static final String CompareFirmwareVersion = "1.12.1029";
    public String NowFirmwareVersion = "";
    public boolean isVersionBig = false;//檢查版本大於等於

    public void CheckFirmwareData(String NowFirmwareVersion) {
        this.NowFirmwareVersion = NowFirmwareVersion;
        String[] NowFirmwareVersionArry = NowFirmwareVersion.split("\\.");
        String[] CompareFirmwareVersionArry = CompareFirmwareVersion.split("\\.");

        for (int i = 0; i < NowFirmwareVersionArry.length; i++) {
            Integer CompareInteger = Integer.valueOf(CompareFirmwareVersionArry[i]);
            Integer NowInteger = Integer.valueOf(NowFirmwareVersionArry[i]);
            if (NowInteger > CompareInteger) {
                isVersionBig = true;
                break;
            }
        }

        if (NowFirmwareVersion.contentEquals(CompareFirmwareVersion)) {
            isVersionBig = true;
        }
    }

    @Override
    public String toString() {
        return "FirmwareData{" +
                "NowFirmwareVersion='" + NowFirmwareVersion + '\'' +
                ", isVersionBig=" + isVersionBig +
                '}';
    }
}
