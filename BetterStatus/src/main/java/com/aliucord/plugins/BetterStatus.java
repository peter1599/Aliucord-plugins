package com.aliucord.plugins;

import android.content.Context;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import androidx.annotation.NonNull;

import androidx.core.content.res.ResourcesCompat;
import com.aliucord.entities.Plugin;
import com.discord.api.presence.ClientStatuses;
import com.aliucord.plugins.betterstatus.PresenceUtils;
import com.discord.models.presence.Presence;
import androidx.appcompat.widget.AppCompatImageView;
import android.widget.Toast; 

import java.util.*;

@SuppressWarnings("unused")
public class BetterStatus extends Plugin {

    public BetterStatus() {
        needsResources = true;
    }

    @NonNull
    @Override
    public Manifest getManifest() {
        Manifest manifest = new Manifest();
        manifest.authors = new Manifest.Author[]{ new Manifest.Author("Butterfly3ffect", 575606699553980430L) };
        manifest.description = "Changes Discord default status icons (online,idle etc..) to better ones.";
        manifest.version = "1.0.0";
        manifest.updateUrl = "https://raw.githubusercontent.com/peter1599/Aliucord-plugins/builds/updater.json";
        return manifest;
    }

    private static final String className = "com.discord.views.StatusView";
    public static Map<String, List<String>> getClassesToPatch() {
        Map<String, List<String>> map = new HashMap<>();
        map.put(className, Collections.singletonList("setPresence"));
        return map;
    }  

    public void setImageResource(AppCompatImageView appCompatImageView, int imageResource)
    {
        appCompatImageView.setImageResource(imageResource);
    }

    public void setImageDrawable(AppCompatImageView appCompatImageView, Drawable imageResource)
    {
        appCompatImageView.setImageDrawable(imageResource);
    }

    public void setImageDrawableWidth(ImageView imageView, int width)
    {
        imageView.getLayoutParams().width = width;
    }

    @Override
    public void start(Context context) {
        //Toast.makeText(context,"Juby check twitter DM's. It's important",Toast.LENGTH_LONG).show();
        
        Drawable isWeb = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_web", "drawable", "com.aliucord.plugins"), null);
        Drawable isWebDND = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_web_dnd", "drawable", "com.aliucord.plugins"), null);
        Drawable isWebIDLE = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_web_idle", "drawable", "com.aliucord.plugins"), null);
        //-----
        Drawable isDesktop = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_desktop", "drawable", "com.aliucord.plugins"), null);
        Drawable isDesktopDND = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_desktop_dnd", "drawable", "com.aliucord.plugins"), null);
        Drawable isDesktopIDLE = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_desktop_idle", "drawable", "com.aliucord.plugins"), null);
        //-----
        Drawable isMobileDND = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_mobile_dnd", "drawable", "com.aliucord.plugins"), null);
        Drawable isMobileIDLE = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_mobile_idle", "drawable", "com.aliucord.plugins"), null);
        //-----
        Drawable isWebMobile = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_web_and_mobile", "drawable", "com.aliucord.plugins"), null);
        Drawable isWebMobileDND = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_web_and_mobile_dnd", "drawable", "com.aliucord.plugins"), null);
        Drawable isWebMobileIDLE = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_web_and_mobile_idle", "drawable", "com.aliucord.plugins"), null);
        //-----
        Drawable isDesktopMobile = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_desktop_and_mobile", "drawable", "com.aliucord.plugins"), null);
        Drawable isDesktopMobileDND = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_desktop_and_mobile_dnd", "drawable", "com.aliucord.plugins"), null);
        Drawable isDesktopMobileIDLE = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_desktop_and_mobile_idle", "drawable", "com.aliucord.plugins"), null);
        //-----
        Drawable isDesktopWeb = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_desktop_and_web", "drawable", "com.aliucord.plugins"), null);
        Drawable isDesktopWebDND = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_desktop_and_web_dnd", "drawable", "com.aliucord.plugins"), null);
        Drawable isDesktopWebIDLE = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_desktop_and_web_idle", "drawable", "com.aliucord.plugins"), null);
        //-----
        Drawable isDesktopWebMobile = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_desktop_web_mobile", "drawable", "com.aliucord.plugins"), null);
        Drawable isDesktopWebMobileDND = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_desktop_web_mobile_dnd", "drawable", "com.aliucord.plugins"), null);
        Drawable isDesktopWebMobileIDLE = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_desktop_web_mobile_idle", "drawable", "com.aliucord.plugins"), null);
        //-----

        patcher.patch(className, "setPresence", (_this, args, ret) -> {
            Drawable imageResourceD = null;
            int imageResource = 0;
            Presence presence = (Presence) args.get(0);
            if(presence == null) return ret;
            ClientStatuses clientStatuses = presence.getClientStatuses();

            if(presence != null) {
                if (clientStatuses != null && PresenceUtils.INSTANCE.isWeb(clientStatuses)) {
                    imageResourceD = isWeb;
                    //setImageDrawableWidth((ImageView) _this, 30);
                }
                if (clientStatuses != null && PresenceUtils.INSTANCE.isWebDND(clientStatuses)) {
                    imageResourceD = isWebDND;
                    //setImageDrawableWidth((ImageView) _this, 30);
                }
                if (clientStatuses != null && PresenceUtils.INSTANCE.isWebIDLE(clientStatuses)) {
                    imageResourceD = isWebIDLE;
                    //setImageDrawableWidth((ImageView) _this, 30);
                }
                //-----
                if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktop(clientStatuses)) {
                    imageResourceD = isDesktop;
                    //setImageDrawableWidth((ImageView) _this, 30);
                }
                if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopDND(clientStatuses)) {
                    imageResourceD = isDesktopDND;
                    //setImageDrawableWidth((ImageView) _this, 30);
                }
                if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopIDLE(clientStatuses)) {
                    imageResourceD = isDesktopIDLE;
                    //setImageDrawableWidth((ImageView) _this, 30);
                }
                //-----
                /*if (clientStatuses != null && PresenceUtils.INSTANCE.isMobile(clientStatuses)) {
                    imageResource = (int) _this;
                    //setImageDrawableWidth((ImageView) _this, 30);
                }*/
                if (clientStatuses != null && PresenceUtils.INSTANCE.isMobileDND(clientStatuses)) {
                    imageResourceD = isMobileDND;
                    //setImageDrawableWidth((ImageView) _this, 30);
                }
                if (clientStatuses != null && PresenceUtils.INSTANCE.isMobileIDLE(clientStatuses)) {
                    imageResourceD = isMobileIDLE;
                    //setImageDrawableWidth((ImageView) _this, 30);
                }
                //-----
                if (clientStatuses != null && PresenceUtils.INSTANCE.isWebMobile(clientStatuses)) {
                    imageResourceD = isWebMobile;
                    //setImageDrawableWidth((ImageView) _this, 48);
                }
                if (clientStatuses != null && PresenceUtils.INSTANCE.isWebMobileDND(clientStatuses)) {
                    imageResourceD = isWebMobileDND;
                    //setImageDrawableWidth((ImageView) _this, 48);
                }
                if (clientStatuses != null && PresenceUtils.INSTANCE.isWebMobileIDLE(clientStatuses)) {
                    imageResourceD = isWebMobileIDLE;
                    //setImageDrawableWidth((ImageView) _this, 48);
                }
                //-----
                if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndMobile(clientStatuses)) {
                    imageResourceD = isDesktopMobile;
                    //setImageDrawableWidth((ImageView) _this, 48);
                }
                if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndMobileDND(clientStatuses)) {
                    imageResourceD = isDesktopMobileDND;
                    //setImageDrawableWidth((ImageView) _this, 48);
                }
                if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndMobileIDLE(clientStatuses)) {
                    imageResourceD = isDesktopMobileIDLE;
                    //setImageDrawableWidth((ImageView) _this, 48);
                }
                //-----
                if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndWeb(clientStatuses)) {
                    imageResourceD = isDesktopWeb;
                    //setImageDrawableWidth((ImageView) _this, 48);
                }
                if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndWebDND(clientStatuses)) {
                    imageResourceD = isDesktopWebDND;
                    //setImageDrawableWidth((ImageView) _this, 48);
                }
                if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndWebIDLE(clientStatuses)) {
                    imageResourceD = isDesktopWebIDLE;
                    //setImageDrawableWidth((ImageView) _this, 48);
                }
                //-----
                if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopWebMobile(clientStatuses)) {
                    imageResourceD = isDesktopWebMobile;
                    //setImageDrawableWidth((ImageView) _this, 64);
                }
                if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopWebMobileDND(clientStatuses)) {
                    imageResourceD = isDesktopWebMobileDND;
                    //setImageDrawableWidth((ImageView) _this, 64);
                }
                if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopWebMobileIDLE(clientStatuses)) {
                    imageResourceD = isDesktopWebMobileIDLE;
                    //setImageDrawableWidth((ImageView) _this, 64);
                }
                //-----
                if(imageResourceD == null) return ret;
                setImageDrawable((AppCompatImageView) _this, imageResourceD);
            }
            return ret;
        });
    }

    @Override
    public void stop(Context context) {
        patcher.unpatchAll();
    }
}
