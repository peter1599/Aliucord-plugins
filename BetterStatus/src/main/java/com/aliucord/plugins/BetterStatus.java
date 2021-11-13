package com.aliucord.plugins;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.res.ResourcesCompat;

import com.aliucord.Logger;
import com.aliucord.PluginManager;
import com.aliucord.Utils;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.api.SettingsAPI;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.Hook;
import com.aliucord.plugins.betterstatus.PresenceUtils;
import com.aliucord.widgets.BottomSheet;
import com.discord.api.presence.ClientStatuses;
import com.discord.databinding.WidgetChannelsListItemChannelPrivateBinding;
import com.discord.models.presence.Presence;
import com.discord.views.CheckedSetting;
import com.discord.views.StatusView;
import com.discord.views.user.UserAvatarPresenceView;
import com.discord.widgets.channels.list.WidgetChannelsListAdapter;
import com.discord.widgets.channels.list.items.ChannelListItem;
import com.discord.widgets.channels.list.items.ChannelListItemPrivate;
import com.discord.widgets.user.profile.UserProfileHeaderView;
import com.discord.widgets.user.profile.UserProfileHeaderViewModel;
import com.facebook.drawee.span.DraweeSpanStringBuilder;
import com.facebook.drawee.span.SimpleDraweeSpanTextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
@AliucordPlugin
public class BetterStatus extends Plugin {

    public BetterStatus() {
        needsResources = true;
        settingsTab = new SettingsTab(BetterStatusSettings.class, SettingsTab.Type.BOTTOM_SHEET).withArgs(settings);
    }
    public Logger logger = new Logger("BetterStatus");

    public void setImageResource(AppCompatImageView appCompatImageView, int imageResource)
    {
        appCompatImageView.setImageResource(imageResource);
    }

    @SuppressLint("ResourceType")
    public void setImageDrawable(AppCompatImageView appCompatImageView, Drawable imageResource)
    {
        appCompatImageView.setImageDrawable(imageResource);
        appCompatImageView.setAdjustViewBounds(true);
        appCompatImageView.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    public void setImageDrawableAround(AppCompatImageView appCompatImageView, int visibility) {
        appCompatImageView.setVisibility(visibility);
    }

    public void setImageDrawable2(AppCompatImageView appCompatImageView, Drawable imageResource, int width)
    {
        appCompatImageView.setImageDrawable(imageResource);
        appCompatImageView.setAdjustViewBounds(true);
        appCompatImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        appCompatImageView.getLayoutParams().width = width;
    }

    @SuppressLint("ResourceType")
    public void setImageDrawableWidth2(AppCompatImageView appCompatImageView, Drawable imageResource, int width) {
        AppCompatImageView status2 = appCompatImageView.findViewById(0x7f0a0209);
        appCompatImageView.setImageDrawable(imageResource);
        status2.getLayoutParams().width = width;
    }

    @SuppressLint("ResourceType")
    @Override
    public void start(Context context) throws Throwable {
        VectorDrawable isWeb = (VectorDrawable)ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_web", "drawable", "com.aliucord.plugins"), null);
        VectorDrawable isWebDND =(VectorDrawable) ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_web_dnd", "drawable", "com.aliucord.plugins"), null);
        VectorDrawable isWebIDLE =(VectorDrawable) ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_web_idle", "drawable", "com.aliucord.plugins"), null);
        //-----
        VectorDrawable isDesktop = (VectorDrawable) ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_desktop", "drawable", "com.aliucord.plugins"), null);
        VectorDrawable isDesktopDND = (VectorDrawable)ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_desktop_dnd", "drawable", "com.aliucord.plugins"), null);
        VectorDrawable isDesktopIDLE = (VectorDrawable)ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_desktop_idle", "drawable", "com.aliucord.plugins"), null);
        //-----
        VectorDrawable isMobile =(VectorDrawable) ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_mobile", "drawable", "com.aliucord.plugins"), null);
        VectorDrawable isMobileDND =(VectorDrawable) ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_mobile_dnd", "drawable", "com.aliucord.plugins"), null);
        VectorDrawable isMobileIDLE = (VectorDrawable)ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_mobile_idle", "drawable", "com.aliucord.plugins"), null);
        //-----
        VectorDrawable isWebMobile =(VectorDrawable) ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_web_mobile", "drawable", "com.aliucord.plugins"), null);
        VectorDrawable isWebMobileDND =(VectorDrawable) ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_web_mobile_dnd", "drawable", "com.aliucord.plugins"), null);
        VectorDrawable isWebMobileIDLE = (VectorDrawable)ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_web_mobile_idle", "drawable", "com.aliucord.plugins"), null);
        //-----
        VectorDrawable isDesktopMobile = (VectorDrawable)ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_desktop_mobile", "drawable", "com.aliucord.plugins"), null);
        VectorDrawable isDesktopMobileDND =(VectorDrawable) ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_desktop_mobile_dnd", "drawable", "com.aliucord.plugins"), null);
        VectorDrawable isDesktopMobileIDLE = (VectorDrawable)ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_desktop_mobile_idle", "drawable", "com.aliucord.plugins"), null);
        //-----
        VectorDrawable isDesktopWeb =(VectorDrawable) ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_desktop_web", "drawable", "com.aliucord.plugins"), null);
        VectorDrawable isDesktopWebDND = (VectorDrawable)ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_desktop_web_dnd", "drawable", "com.aliucord.plugins"), null);
        VectorDrawable isDesktopWebIDLE =(VectorDrawable) ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_desktop_web_idle", "drawable", "com.aliucord.plugins"), null);
        //-----
        VectorDrawable isDesktopWebMobile = (VectorDrawable)ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_desktop_web_mobile", "drawable", "com.aliucord.plugins"), null);
        VectorDrawable isDesktopWebMobileDND = (VectorDrawable)ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_desktop_web_mobile_dnd", "drawable", "com.aliucord.plugins"), null);
        VectorDrawable isDesktopWebMobileIDLE = (VectorDrawable)ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_desktop_web_mobile_idle", "drawable", "com.aliucord.plugins"), null);
        //-----
        if (!settings.getBool("filled_colors", false)) {
            patcher.patch(StatusView.class.getDeclaredMethod("setPresence", Presence.class), new Hook(callFrame -> {
                Presence presence = (Presence) callFrame.args[0];
                if (presence == null) return;
                ClientStatuses clientStatuses = presence.getClientStatuses();

                if (clientStatuses != null) {
                    List<VectorDrawable> drawableList = new ArrayList<>();
                    var desktopStatus = clientStatuses.a();
                    var mobileStatus = clientStatuses.b();
                    var webStatus = clientStatuses.c();

                    switch (webStatus) {
                        case ONLINE:
                            drawableList.add(isWeb);
                            break;
                        case DND:
                            drawableList.add(isWebDND);
                            break;
                        case IDLE:
                            drawableList.add(isWebIDLE);
                    }

                    switch (desktopStatus) {
                        case ONLINE:
                            drawableList.add(isDesktop);
                            break;
                        case DND:
                            drawableList.add(isDesktopDND);
                            break;
                        case IDLE:
                            drawableList.add(isDesktopIDLE);
                            break;
                    }

                    switch (mobileStatus) {
                        case ONLINE:
                            drawableList.add(isMobile);
                            break;
                        case DND:
                            drawableList.add(isMobileDND);
                            break;
                        case IDLE:
                            drawableList.add(isMobileIDLE);
                    }


                    View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent();

                    if (drawableList.size() == 1) {
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, drawableList.get(0));
                    } else if (drawableList.size() > 1) {
                        SimpleDraweeSpanTextView username_status = parent.findViewById(Utils.getResId("username_text", "id"));
                        if (username_status!=null){
                            for (VectorDrawable drawabe : drawableList) {

                                DraweeSpanStringBuilder icon = new DraweeSpanStringBuilder();
                                icon.append(" ", new ImageSpan(drawabe, 1), 0);
                                username_status.append(" ");
                                username_status.append(icon);
                            }
                        }

                    }
                }
            }));

            //--------UserProfileHeaderView---------

            try {
                patcher.patch(UserProfileHeaderView.class.getDeclaredMethod("updateViewState", UserProfileHeaderViewModel.ViewState.Loaded.class), new Hook(callFrame1 ->
                {
                    try {
                        Presence presence1 = ((UserProfileHeaderViewModel.ViewState.Loaded) callFrame1.args[0]).getPresence();
                        View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                        TextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));

                        if (presence1 == null) {
                            username_profile_header2.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                            return;
                        }

                        ClientStatuses clientStatuses1 = presence1.getClientStatuses();
                        if (clientStatuses1 != null) {
                            username_profile_header2.setCompoundDrawablePadding(2);
                            if (PresenceUtils.INSTANCE.isWebMobile(clientStatuses1)) { username_profile_header2.setCompoundDrawablesWithIntrinsicBounds(null, null, isWebMobile, null); }
                            if (PresenceUtils.INSTANCE.isWebMobileDND(clientStatuses1)) { username_profile_header2.setCompoundDrawablesWithIntrinsicBounds(null, null, isWebMobileDND, null); }
                            if (PresenceUtils.INSTANCE.isWebMobileIDLE(clientStatuses1)) { username_profile_header2.setCompoundDrawablesWithIntrinsicBounds(null, null, isWebMobileIDLE, null); }
                            //-------------------
                            if (PresenceUtils.INSTANCE.isDesktopAndMobile(clientStatuses1)) { username_profile_header2.setCompoundDrawablesWithIntrinsicBounds(null, null, isDesktopMobile, null); }
                            if (PresenceUtils.INSTANCE.isDesktopAndMobileDND(clientStatuses1)) { username_profile_header2.setCompoundDrawablesWithIntrinsicBounds(null, null, isDesktopMobileDND, null); }
                            if (PresenceUtils.INSTANCE.isDesktopAndMobileIDLE(clientStatuses1)) { username_profile_header2.setCompoundDrawablesWithIntrinsicBounds(null, null, isDesktopMobileIDLE, null); }
                            //-------
                            if (PresenceUtils.INSTANCE.isDesktopAndWeb(clientStatuses1)) { username_profile_header2.setCompoundDrawablesWithIntrinsicBounds(null, null, isDesktopWeb, null); }
                            if (PresenceUtils.INSTANCE.isDesktopAndWebDND(clientStatuses1)) { username_profile_header2.setCompoundDrawablesWithIntrinsicBounds(null, null, isDesktopWebDND, null); }
                            if (PresenceUtils.INSTANCE.isDesktopAndWebIDLE(clientStatuses1)) { username_profile_header2.setCompoundDrawablesWithIntrinsicBounds(null, null, isDesktopWebIDLE, null); }
                            //-------
                            if (PresenceUtils.INSTANCE.isDesktopWebMobile(clientStatuses1)) { username_profile_header2.setCompoundDrawablesWithIntrinsicBounds(null, null, isDesktopWebMobile, null); }
                            if (PresenceUtils.INSTANCE.isDesktopWebMobileDND(clientStatuses1)) { username_profile_header2.setCompoundDrawablesWithIntrinsicBounds(null, null, isDesktopWebMobileDND, null); }
                            if (PresenceUtils.INSTANCE.isDesktopWebMobileIDLE(clientStatuses1)) { username_profile_header2.setCompoundDrawablesWithIntrinsicBounds(null, null, isDesktopWebMobileIDLE, null); }
                        }
                    } catch (Throwable e) {
                        logger.error("An error occurred in UserProfileHeaderView", e);
                    }
                }));
            } catch (Throwable e) {
                logger.error("An error occurred in UserProfileHeaderView", e);
            }

            //----------RADIAL STATUS----------

            if ((PluginManager.plugins.containsKey("SquareAvatars") && !PluginManager.isPluginEnabled("SquareAvatars"))) {
                if (settings.getBool("radial_status_up", true)) {
                    //-----------Radial Status on UserProfileHeaderView--------------

                    patcher.patch(UserAvatarPresenceView.class.getDeclaredMethod("a", UserAvatarPresenceView.a.class), new Hook(callFrame -> {
                        UserAvatarPresenceView.a data = (UserAvatarPresenceView.a) callFrame.args[0];

                        View avatar = (View) ((UserAvatarPresenceView) callFrame.thisObject).getParent();
                        ImageView avatar2 = avatar.findViewById(Utils.getResId("avatar_cutout", "id"));

                        if (data.b == null) {
                            avatar2.setPadding(0, 0, 0, 0);
                            avatar2.setBackground(null);
                        }

                        ClientStatuses clientStatuses = data.b.getClientStatuses();

                        if (clientStatuses != null) {
                            setRadialStatus(clientStatuses, avatar2);
                        }
                    }));
                }
            }

            if (PluginManager.plugins.containsKey("SquareAvatars") && PluginManager.isPluginEnabled("SquareAvatars")) {
                if (settings.getBool("radial_status_up", true)) {
                    //-----------Radial Status on UserProfileHeaderView--------------

                    patcher.patch(UserAvatarPresenceView.class.getDeclaredMethod("a", UserAvatarPresenceView.a.class), new Hook(callFrame -> {
                        UserAvatarPresenceView.a data = (UserAvatarPresenceView.a) callFrame.args[0];
                        if (data.b == null) return;
                        ClientStatuses clientStatuses = data.b.getClientStatuses();

                        if (clientStatuses != null) {

                            View avatar = (View) ((UserAvatarPresenceView) callFrame.thisObject).getParent();
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("avatar", "id"));
                            ImageView avatar2_cut = avatar.findViewById(Utils.getResId("avatar_cutout", "id"));

                            setSquareStatus(clientStatuses, avatar2);

                            avatar2_cut.setVisibility(View.INVISIBLE);

                        }
                    }));
                }
            }

            //----------ChannelMemberList---------

            if (!(PluginManager.plugins.containsKey("SquareAvatars")) || !PluginManager.isPluginEnabled("SquareAvatars")) {
                if (settings.getBool("radial_status_cml", true)) {
                    //-----ChannelMemberList--------

                    patcher.patch(StatusView.class.getDeclaredMethod("setPresence", Presence.class), new Hook(callFrame -> {
                        Presence presence = (Presence) callFrame.args[0];

                        View avatar = (View) ((StatusView) callFrame.thisObject).getParent(); //UsernameView class or method?
                        ImageView avatar2 = avatar.findViewById(Utils.getResId("channel_members_list_item_avatar", "id"));

                        if (presence == null) {
                            if (avatar2 != null) {
                                avatar2.setPadding(0, 0, 0, 0);
                                avatar2.setBackground(null);
                            }
                            return;
                        }
                        ClientStatuses clientStatuses = presence.getClientStatuses();

                        if (clientStatuses != null && avatar2!=null) {
                            setRadialStatus(clientStatuses, avatar2);
                        }
                    }));

                }
            }

            if (PluginManager.plugins.containsKey("SquareAvatars") && PluginManager.isPluginEnabled("SquareAvatars")) {
                if (settings.getBool("radial_status_cml", true)) {
                    //-----ChannelMemberList--------
                    patcher.patch(StatusView.class.getDeclaredMethod("setPresence", Presence.class), new Hook(callFrame -> {
                        Presence presence = (Presence) callFrame.args[0];

                        View avatar = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                        ImageView avatar2 = avatar.findViewById(Utils.getResId("channel_members_list_item_avatar", "id"));
                        if (presence == null ) {
                            if ( avatar2!=null){
                                avatar2.setPadding(0, 0, 0, 0);
                                avatar2.setBackground(null);
                            }
                            return;
                        }
                        ClientStatuses clientStatuses = presence.getClientStatuses();

                        if (clientStatuses != null && avatar2!=null) {
                            setSquareStatus(clientStatuses, avatar2);
                        }
                    }));
                }
            }

            //-------------DM List--------

            if (!PluginManager.plugins.containsKey("SquareAvatars") || !PluginManager.isPluginEnabled("SquareAvatars")) {
                if (settings.getBool("radial_status_dm", true)) {

                    patcher.patch(WidgetChannelsListAdapter.ItemChannelPrivate.class.getDeclaredMethod("onConfigure", int.class, ChannelListItem.class), new Hook(callFrame -> {

                        Field bindingField = null;
                        try {
                            bindingField = WidgetChannelsListAdapter.ItemChannelPrivate.class.getDeclaredField("binding");
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        }
                        bindingField.setAccessible(true);
                        WidgetChannelsListItemChannelPrivateBinding binding = null;

                        try {
                            binding = (WidgetChannelsListItemChannelPrivateBinding) bindingField.get(callFrame.thisObject);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }

                        RelativeLayout layout = (RelativeLayout) binding.getRoot();

                        ChannelListItemPrivate data = (ChannelListItemPrivate) callFrame.args[1];
                        Presence presence = data.getPresence();
                        ImageView avatar2 = layout.findViewById(Utils.getResId("channels_list_item_private_avatar", "id"));
                        Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                        if (presence == null) {
                            avatar2.setPadding(0, 0, 0, 0);
                            avatar2.setBackground(null);
                        }
                        ClientStatuses clientStatuses = presence.getClientStatuses();

                        if (clientStatuses != null) setRadialStatus(clientStatuses, avatar2);
                    }));
                }
            }

            if (PluginManager.plugins.containsKey("SquareAvatars") && PluginManager.isPluginEnabled("SquareAvatars")) {
                if (settings.getBool("radial_status_dm", true)) {
                    patcher.patch(WidgetChannelsListAdapter.ItemChannelPrivate.class.getDeclaredMethod("onConfigure", int.class, ChannelListItem.class), new Hook(callFrame -> {

                        Field bindingField = null;
                        try {
                            bindingField = WidgetChannelsListAdapter.ItemChannelPrivate.class.getDeclaredField("binding");
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        }
                        bindingField.setAccessible(true);
                        WidgetChannelsListItemChannelPrivateBinding binding = null;
                        try {
                            binding = (WidgetChannelsListItemChannelPrivateBinding) bindingField.get(callFrame.thisObject);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }


                        RelativeLayout layout = (RelativeLayout) binding.getRoot();
                        ChannelListItemPrivate data = (ChannelListItemPrivate) callFrame.args[1];
                        Presence presence = data.getPresence();

                        ImageView avatar2 = layout.findViewById(Utils.getResId("channels_list_item_private_avatar", "id"));


                        if (presence == null) {
                            avatar2.setPadding(0, 0, 0, 0);
                            avatar2.setBackground(null);
                        }

                        ClientStatuses clientStatuses = presence.getClientStatuses();
                        if (clientStatuses != null) {
                            setSquareStatus(clientStatuses, avatar2);
                        }
                    }));

                }

            }

            //---------END--------
        } else {
            //if (settings.getBool("filled_colors", true) == true)
            patcher.patch(StatusView.class.getDeclaredMethod("setPresence", Presence.class), new Hook(callFrame -> {
                Drawable imageResourceD = null;

                int imageResource = 0;
                Presence presence = (Presence) callFrame.args[0];
                if (presence == null) return;

                ClientStatuses clientStatuses = presence.getClientStatuses();

                if (clientStatuses != null) {
                    var desktopStatus = clientStatuses.a();
                    var mobileStatus = clientStatuses.b();
                    var webStatus = clientStatuses.c();
                    switch (webStatus) {
                        case ONLINE: setImageDrawable((AppCompatImageView) callFrame.thisObject, getDrawable("ic_online"));break;
                        case DND: setImageDrawable((AppCompatImageView) callFrame.thisObject, getDrawable("ic_dnd"));break;
                        case IDLE: setImageDrawable((AppCompatImageView) callFrame.thisObject, getDrawable("ic_idle"));break;
                    }
                    switch (mobileStatus) {
                        case ONLINE: setImageDrawable((AppCompatImageView) callFrame.thisObject, getDrawable("ic_mobile"));break;
                        case DND: setImageDrawable((AppCompatImageView) callFrame.thisObject, getDrawable("ic_dnd"));break;
                        case IDLE: setImageDrawable((AppCompatImageView) callFrame.thisObject, getDrawable("ic_idle"));break;
                    }
                    switch (desktopStatus) {
                        case ONLINE: setImageDrawable((AppCompatImageView) callFrame.thisObject, getDrawable("ic_online"));break;
                        case DND: setImageDrawable((AppCompatImageView) callFrame.thisObject, getDrawable("ic_dnd"));break;
                        case IDLE: setImageDrawable((AppCompatImageView) callFrame.thisObject, getDrawable("ic_idle"));break;
                    }
                }
            }));
        }
    }

    public Drawable getDrawable(String name) {
        return ResourcesCompat.getDrawable(resources, resources.getIdentifier(name, "drawable", "com.aliucord.plugins"), null);

    }

    public void setSquareStatus(ClientStatuses clientStatuses, View avatar) {
        Drawable square_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_profile_rectangle_status", "drawable", "com.aliucord.plugins"), null).mutate();
        setStatus(clientStatuses, avatar, square_status);
    }

    public void setRadialStatus(ClientStatuses clientStatuses, View avatar) {
        Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_profile_radial_status", "drawable", "com.aliucord.plugins"), null).mutate();
        setStatus(clientStatuses, avatar, radial_status);
    }

    public void setStatus(ClientStatuses clientStatuses, View avatar, Drawable background /* (UserAvatarPresenceView) */) {

        var desktopStatus = clientStatuses.a();
        var mobileStatus = clientStatuses.b();
        var webStatus = clientStatuses.c();

        avatar.setPadding(8, 8, 8, 8);
        avatar.setBackground(background);

        switch (webStatus) {
            case ONLINE:
                Objects.requireNonNull(background).setTint(0xFF3BA55C);
                break;
            case DND:
                Objects.requireNonNull(background).setTint(0xFFED4245);
                break;
            case IDLE:
                Objects.requireNonNull(background).setTint(0xFFFAA61A - 1);
                break;
        }

        switch (desktopStatus) {
            case ONLINE:
                Objects.requireNonNull(background).setTint(0xFF3BA55C);
                break;
            case DND:
                Objects.requireNonNull(background).setTint(0xFFED4245);
                break;
            case IDLE:
                Objects.requireNonNull(background).setTint(0xFFFAA61A - 1);
                break;
        }

        switch (mobileStatus) {
            case ONLINE:
                Objects.requireNonNull(background).setTint(0xFF3BA55C);
                break;
            case DND:
                Objects.requireNonNull(background).setTint(0xFFED4245);
                break;
            case IDLE:
                Objects.requireNonNull(background).setTint(0xFFFAA61A - 1);
                break;
        }
    }

    @Override
    public void stop(Context context) {
        patcher.unpatchAll();
    }

    public static class BetterStatusSettings extends BottomSheet {
        private final SettingsAPI settings;
        public BetterStatusSettings(SettingsAPI settings) {
            this.settings = settings;
        }
        public void onViewCreated(View view, Bundle bundle) {

            super.onViewCreated(view, bundle);
            CheckedSetting filled_status = Utils.createCheckedSetting(requireContext(), CheckedSetting.ViewType.SWITCH, "Filled Colors", "Uses filled colors for status.");
            filled_status.setChecked(false);
            filled_status.setChecked(settings.getBool("filled_colors", false));
            filled_status.setOnCheckedListener(checked -> {
                settings.setBool("filled_colors", checked);
                Utils.showToast("Please restart Aliucord to apply");
            });
            addView(filled_status);
            //------------------------------

            CheckedSetting radial_status_cml = Utils.createCheckedSetting(requireContext(), CheckedSetting.ViewType.SWITCH, "Radial Status (ChannelsMemeberList)", "Shows a status ring around the user avatar in the ChannelsMembersList.");
            radial_status_cml.setChecked(false);
            radial_status_cml.setChecked(settings.getBool("radial_status_cml", false));
            radial_status_cml.setOnCheckedListener(checked -> {
                settings.setBool("radial_status_cml", checked);
                //PluginManager.stopPlugin("BetterStatus");
                //PluginManager.startPlugin("BetterStatus");
                Utils.showToast("Please restart Aliucord to apply");
            });
            addView(radial_status_cml);

            CheckedSetting radial_status_dm = Utils.createCheckedSetting(requireContext(), CheckedSetting.ViewType.SWITCH, "Radial Status (DM's)", "Shows a status ring around the user avatar in the DM List.");
            radial_status_dm.setChecked(false);
            radial_status_dm.setChecked(settings.getBool("radial_status_dm", false));
            radial_status_dm.setOnCheckedListener(checked -> {
                settings.setBool("radial_status_dm", checked);
                Utils.showToast("Please restart Aliucord to apply");
            });
            addView(radial_status_dm);

            CheckedSetting radial_status_up = Utils.createCheckedSetting(requireContext(), CheckedSetting.ViewType.SWITCH, "Radial Status (UserProfile)", "Shows a status ring around the user avatar in the UserProfile.");
            radial_status_up.setChecked(false);
            radial_status_up.setChecked(settings.getBool("radial_status_up", false));
            radial_status_up.setOnCheckedListener(checked -> {
                settings.setBool("radial_status_up", checked);
                Utils.showToast("Please restart Aliucord to apply");
            });
            addView(radial_status_up);

        }
    }
}
