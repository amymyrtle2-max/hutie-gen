package com.hutie.gen;

import android.content.pm.ApplicationInfo;

import java.util.Random;

/**
 * 帖子内容生成器：根据 APK 解析出的信息，随机生成葫芦侠风格的软件贴文案
 */
public class PostGenerator {

    private static final Random RANDOM = new Random();

    public static String randomTitle(String name, String version) {
        switch (RANDOM.nextInt(5)) {
            case 0:
                return "【安卓】" + name + " " + version + " 🔥 亲测好用";
            case 1:
                return "【安卓】" + name + " 🔥 " + version + " 新版本";
            case 2:
                return "【安卓】" + name + " " + version + " ✨ 强烈推荐";
            case 3:
                return "【安卓】" + name + " v" + version + " 💯 良心之作";
            default:
                return "【安卓】" + name + " " + version + " 🚀 流畅好用";
        }
    }

    public static String randomOpening(String name, String version, int category) {
        String cat = categoryWord(category);
        switch (RANDOM.nextInt(5)) {
            case 0:
                return name + "是一款非常实用的" + cat + "类应用，" + version + "版本运行流畅、界面清爽，实测体验非常棒，强烈推荐给大家！";
            case 1:
                return "今天给葫芦们分享一款神器——" + name + "！功能强大又完全免费，新手也能秒上手，用过的都说好！";
            case 2:
                return "最近挖到一款宝藏应用" + name + "，" + version + "版本稳定流畅，关键功能全开放，必须给大伙安排一波！";
            case 3:
                return "还在找好用的" + cat + "类软件？这款" + name + "千万别错过，体积小、速度快，装上就离不开了！";
            default:
                return name + "新版本" + version + "来啦！本次更新体验更上一层楼，操作丝滑不卡顿，福利软件先到先得！";
        }
    }

    public static String randomIntro(String name, String version, String size, int category) {
        String cat = categoryWord(category);
        switch (RANDOM.nextInt(5)) {
            case 0:
                return name + " v" + version + "，安装包仅" + size + "，小巧不占内存。功能齐全、运行流畅，绿色安全，喜欢的葫芦们赶紧下载体验吧！";
            case 1:
                return "软件版本：" + version + "｜安装大小：" + size + "\n界面清爽无广告，功能实用不花哨，兼容性强，主流机型均可流畅运行。";
            case 2:
                return name + "最新版本" + version + "，安装包" + size + "。下载安装即可使用，打开就能用，非常方便！";
            case 3:
                return "亲测好用的" + cat + "类应用！版本" + version + "，大小" + size + "。启动快、运行稳，日常必备神器，建议收藏备用！";
            default:
                return name + " " + version + " 版本，大小" + size + "，不占空间。功能全免费，操作简单，拿走不谢！";
        }
    }

    public static String categoryWord(int category) {
        switch (category) {
            case ApplicationInfo.CATEGORY_GAME:
                return "游戏";
            case ApplicationInfo.CATEGORY_AUDIO:
                return "影音";
            case ApplicationInfo.CATEGORY_VIDEO:
                return "视频";
            case ApplicationInfo.CATEGORY_IMAGE:
                return "图片";
            case ApplicationInfo.CATEGORY_SOCIAL:
                return "社交";
            case ApplicationInfo.CATEGORY_NEWS:
                return "资讯";
            case ApplicationInfo.CATEGORY_MAPS:
                return "导航";
            case ApplicationInfo.CATEGORY_PRODUCTIVITY:
                return "效率";
            default:
                return "实用";
        }
    }

    /**
     * 按葫芦侠软件贴格式拼接完整帖子
     */
    public static String buildFullPost(String title, String opening, String name, String version,
                                       String size, String intro, String link1, String link2) {
        if (link1 == null || link1.isEmpty()) link1 = "（请在此粘贴网盘链接）";
        if (link2 == null || link2.isEmpty()) link2 = "（请在此粘贴备用链接）";
        return title + "\n\n"
                + opening + "\n\n"
                + "（此处插入软件截图）\n\n"
                + "【软件名称】：" + name + "\n\n"
                + "【软件版本】：" + version + "\n\n"
                + "【软件大小】：" + size + "\n\n"
                + "【软件介绍】：\n" + intro + "\n\n"
                + "∧＿∧\n"
                + "（｀･ω･´)づ＿∧\n"
                + "（つ　 / (・ω・。)\n"
                + "しーＪ (nnノ) pat pat\n\n"
                + "【✨下载安装即可使用！！！】\n\n"
                + "【下载链接】：\n" + link1 + "\n（防止链接丢失，先保存再下载📌）\n\n"
                + "【备用链接】：\n" + link2 + "\n\n"
                + "拿走记得吱一声！🐯\n拿走记得吱一声！🐯\n拿走记得吱一声！🐯\n\n"
                + "首评有葫芦奖励发放₍˄·͈༝·͈˄*₎◞ ̑̑";
    }
}
