package com.aliucord.plugins;

import android.annotation.SuppressLint;
import android.content.Context;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import com.aliucord.Logger;
import com.aliucord.Utils;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.api.SettingsAPI;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.InsteadHook;
import com.aliucord.patcher.PinePrePatchFn;
import com.aliucord.widgets.BottomSheet;
import com.discord.api.presence.ClientStatus;
import com.discord.api.presence.ClientStatuses;
import com.aliucord.plugins.betterstatus.PresenceUtils;
import com.aliucord.patcher.PinePatchFn;
import com.discord.app.App;
import com.discord.databinding.UserProfileHeaderViewBinding;
import com.discord.databinding.WidgetChannelMembersListItemUserBinding;
import com.discord.models.member.GuildMember;
import com.discord.models.presence.Presence;
import com.discord.models.user.MeUser;
import com.discord.models.user.User;
import com.discord.stores.Store;
import com.discord.stores.StoreAuthentication;
import com.discord.stores.StoreUser;
import com.discord.stores.StoreUserPresence;
import com.discord.stores.StoreUserPresenceKt;
import com.discord.utilities.color.ColorCompatKt;
import com.discord.views.CheckedSetting;
import com.discord.views.StatusView;
import com.discord.views.UsernameView;
import com.discord.views.user.UserAvatarPresenceView;
import com.discord.widgets.channels.memberlist.WidgetChannelMembersList;
import com.discord.widgets.channels.memberlist.WidgetChannelMembersListViewModel;
import com.discord.widgets.channels.memberlist.adapter.ChannelMembersListAdapter;
import com.discord.widgets.channels.memberlist.adapter.ChannelMembersListViewHolderMember;
import com.discord.widgets.chat.list.WidgetChatList$binding$2;
import com.discord.widgets.chat.list.WidgetChatList$binding$3;
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapter;
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage;
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessageHeader;
import com.discord.widgets.chat.list.entries.ChatListEntry;
import com.discord.widgets.chat.list.entries.MessageEntry;
import com.discord.widgets.servers.settings.members.WidgetServerSettingsMembersAdapter;
import com.discord.widgets.user.profile.UserProfileHeaderView;
import com.discord.widgets.user.profile.UserProfileHeaderViewModel;
import com.discord.widgets.user.profile.UserProfileHeaderViewModel$ViewState$Loaded$avatarColorId$2;
import com.facebook.drawee.span.DraweeSpanStringBuilder;
import com.facebook.drawee.span.SimpleDraweeSpanTextView;

import androidx.appcompat.widget.AppCompatImageView;

import java.lang.reflect.Field;
import java.util.*;

import c.a.i.s1;
import kotlin.jvm.functions.Function0;
import top.canyie.pine.callback.MethodHook;

@SuppressWarnings("unused")
@AliucordPlugin
public class BetterStatus extends Plugin {

    public BetterStatus() {
        needsResources = true;
        settingsTab = new SettingsTab(BetterStatusSettings.class, SettingsTab.Type.BOTTOM_SHEET).withArgs(settings);
    }
    public Logger logger = new Logger("BetterStatus");

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
        //AppCompatImageView status = appCompatImageView.findViewById(0x7F0A01C9);
        appCompatImageView.getLayoutParams().width = width;
    }

    @SuppressLint("ResourceType")
    public void setImageDrawableWidth2(AppCompatImageView appCompatImageView, Drawable imageResource, int width)
    {
        AppCompatImageView status2 = appCompatImageView.findViewById(0x7f0a0209);
        appCompatImageView.setImageDrawable(imageResource);
        status2.getLayoutParams().width = width;
    }

    @SuppressLint("ResourceType")
    @Override
    public void start(Context context) throws Throwable {

        //----------------------------

        Drawable isWeb = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_web", "drawable", "com.aliucord.plugins"), null);
        Drawable isWebDND = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_web_dnd", "drawable", "com.aliucord.plugins"), null);
        Drawable isWebIDLE = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_web_idle", "drawable", "com.aliucord.plugins"), null);
        //-----
        Drawable isDesktop = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_desktop", "drawable", "com.aliucord.plugins"), null);
        Drawable isDesktopDND = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_desktop_dnd", "drawable", "com.aliucord.plugins"), null);
        Drawable isDesktopIDLE = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_desktop_idle", "drawable", "com.aliucord.plugins"), null);
        //-----
        Drawable isMobile = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_mobile", "drawable", "com.aliucord.plugins"), null);
        Drawable isMobileDND = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_mobile_dnd", "drawable", "com.aliucord.plugins"), null);
        Drawable isMobileIDLE = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_mobile_idle", "drawable", "com.aliucord.plugins"), null);
        //-----
        Drawable isWebMobile = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_web_mobile", "drawable", "com.aliucord.plugins"), null);
        Drawable isWebMobileDND = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_web_mobile_dnd", "drawable", "com.aliucord.plugins"), null);
        Drawable isWebMobileIDLE = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_web_mobile_idle", "drawable", "com.aliucord.plugins"), null);
        //-----
        Drawable isDesktopMobile = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_desktop_mobile", "drawable", "com.aliucord.plugins"), null);
        Drawable isDesktopMobileDND = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_desktop_mobile_dnd", "drawable", "com.aliucord.plugins"), null);
        Drawable isDesktopMobileIDLE = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_desktop_mobile_idle", "drawable", "com.aliucord.plugins"), null);
        //-----
        Drawable isDesktopWeb = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_desktop_web", "drawable", "com.aliucord.plugins"), null);
        Drawable isDesktopWebDND = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_desktop_web_dnd", "drawable", "com.aliucord.plugins"), null);
        Drawable isDesktopWebIDLE = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_desktop_web_idle", "drawable", "com.aliucord.plugins"), null);
        //-----
        Drawable isDesktopWebMobile = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_desktop_web_mobile", "drawable", "com.aliucord.plugins"), null);
        Drawable isDesktopWebMobileDND = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_desktop_web_mobile_dnd", "drawable", "com.aliucord.plugins"), null);
        Drawable isDesktopWebMobileIDLE = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_desktop_web_mobile_idle", "drawable", "com.aliucord.plugins"), null);
        //-----

        if (settings.getBool("filled_colors", false) == false) {

            patcher.patch(StatusView.class.getDeclaredMethod("setPresence", Presence.class), new PinePatchFn(callFrame -> {
                
                Drawable imageResourceD = null;

                int imageResource = 0;
                Presence presence = (Presence) callFrame.args[0];

                if (presence == null) return;
                ClientStatuses clientStatuses = presence.getClientStatuses();


                if (presence != null) {
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isWeb(clientStatuses)) {
                        imageResourceD = isWeb;
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isWebDND(clientStatuses)) {
                        imageResourceD = isWebDND;
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isWebIDLE(clientStatuses)) {
                        imageResourceD = isWebIDLE;
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                    }
                    //-----
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktop(clientStatuses)) {
                        imageResourceD = isDesktop;
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopDND(clientStatuses)) {
                        imageResourceD = isDesktopDND;
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopIDLE(clientStatuses)) {
                        imageResourceD = isDesktopIDLE;
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                    }
                    //-----
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isMobile(clientStatuses)) {
                        imageResourceD = isMobile;
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isMobileDND(clientStatuses)) {
                        imageResourceD = isMobileDND;
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isMobileIDLE(clientStatuses)) {
                        imageResourceD = isMobileIDLE;
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                    }
                    //-----
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isWebMobile(clientStatuses)) {

                        View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent();
                        SimpleDraweeSpanTextView username_status = parent.findViewById(Utils.getResId("username_text", "id"));

                        DraweeSpanStringBuilder first_icon = new DraweeSpanStringBuilder();
                        first_icon.append(" ", new ImageSpan(isWeb,1), 0);

                        DraweeSpanStringBuilder second_icon = new DraweeSpanStringBuilder();
                        second_icon.append(" ", new ImageSpan(isMobile,1), 0);

                        username_status.append(" ");
                        username_status.append(first_icon);
                        username_status.append(" ");
                        username_status.append(second_icon);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isWebMobileDND(clientStatuses)) {

                        View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent();
                        SimpleDraweeSpanTextView username_status = parent.findViewById(Utils.getResId("username_text", "id"));

                        DraweeSpanStringBuilder first_icon = new DraweeSpanStringBuilder();
                        first_icon.append(" ", new ImageSpan(isWebDND,1), 0);

                        DraweeSpanStringBuilder second_icon = new DraweeSpanStringBuilder();
                        second_icon.append(" ", new ImageSpan(isMobileDND,1), 0);

                        username_status.append(" ");
                        username_status.append(first_icon);
                        username_status.append(" ");
                        username_status.append(second_icon);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isWebMobileIDLE(clientStatuses)) {

                        View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent();
                        SimpleDraweeSpanTextView username_status = parent.findViewById(Utils.getResId("username_text", "id"));

                        DraweeSpanStringBuilder first_icon = new DraweeSpanStringBuilder();
                        first_icon.append(" ", new ImageSpan(isWebIDLE,1), 0);

                        DraweeSpanStringBuilder second_icon = new DraweeSpanStringBuilder();
                        second_icon.append(" ", new ImageSpan(isMobileIDLE,1), 0);

                        username_status.append(" ");
                        username_status.append(first_icon);
                        username_status.append(" ");
                        username_status.append(second_icon);
                    }
                    //-----
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndMobile(clientStatuses)) {

                        View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent();
                        SimpleDraweeSpanTextView username_status = parent.findViewById(Utils.getResId("username_text", "id"));

                        DraweeSpanStringBuilder first_icon = new DraweeSpanStringBuilder();
                        first_icon.append(" ", new ImageSpan(isDesktop,1), 0);

                        DraweeSpanStringBuilder second_icon = new DraweeSpanStringBuilder();
                        second_icon.append(" ", new ImageSpan(isMobile,1), 0);

                        username_status.append(" ");
                        username_status.append(first_icon);
                        username_status.append(" ");
                        username_status.append(second_icon);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndMobileDND(clientStatuses)) {

                        View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent();
                        SimpleDraweeSpanTextView username_status = parent.findViewById(Utils.getResId("username_text", "id"));

                        DraweeSpanStringBuilder first_icon = new DraweeSpanStringBuilder();
                        first_icon.append(" ", new ImageSpan(isDesktopDND,1), 0);

                        DraweeSpanStringBuilder second_icon = new DraweeSpanStringBuilder();
                        second_icon.append(" ", new ImageSpan(isMobileDND,1), 0);

                        username_status.append(" ");
                        username_status.append(first_icon);
                        username_status.append(" ");
                        username_status.append(second_icon);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndMobileIDLE(clientStatuses)) {

                        View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent();
                        SimpleDraweeSpanTextView username_status = parent.findViewById(Utils.getResId("username_text", "id"));

                        DraweeSpanStringBuilder first_icon = new DraweeSpanStringBuilder();
                        first_icon.append(" ", new ImageSpan(isDesktopIDLE,1), 0);

                        DraweeSpanStringBuilder second_icon = new DraweeSpanStringBuilder();
                        second_icon.append(" ", new ImageSpan(isMobileIDLE,1), 0);

                        username_status.append(" ");
                        username_status.append(first_icon);
                        username_status.append(" ");
                        username_status.append(second_icon);
                    }
                    //-----
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndWeb(clientStatuses)) {

                        View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent();
                        SimpleDraweeSpanTextView username_status = parent.findViewById(Utils.getResId("username_text", "id"));

                        DraweeSpanStringBuilder first_icon = new DraweeSpanStringBuilder();
                        first_icon.append(" ", new ImageSpan(isDesktop,1), 0);

                        DraweeSpanStringBuilder second_icon = new DraweeSpanStringBuilder();
                        second_icon.append(" ", new ImageSpan(isWeb,1), 0);

                        username_status.append(" ");
                        username_status.append(first_icon);
                        username_status.append(" ");
                        username_status.append(second_icon);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndWebDND(clientStatuses)) {

                        View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent();
                        SimpleDraweeSpanTextView username_status = parent.findViewById(Utils.getResId("username_text", "id"));

                        DraweeSpanStringBuilder first_icon = new DraweeSpanStringBuilder();
                        first_icon.append(" ", new ImageSpan(isDesktopDND,1), 0);

                        DraweeSpanStringBuilder second_icon = new DraweeSpanStringBuilder();
                        second_icon.append(" ", new ImageSpan(isWebDND,1), 0);

                        username_status.append(" ");
                        username_status.append(first_icon);
                        username_status.append(" ");
                        username_status.append(second_icon);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndWebIDLE(clientStatuses)) {

                        View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent();
                        SimpleDraweeSpanTextView username_status = parent.findViewById(Utils.getResId("username_text", "id"));

                        DraweeSpanStringBuilder first_icon = new DraweeSpanStringBuilder();
                        first_icon.append(" ", new ImageSpan(isDesktopIDLE,1), 0);

                        DraweeSpanStringBuilder second_icon = new DraweeSpanStringBuilder();
                        second_icon.append(" ", new ImageSpan(isWebIDLE,1), 0);

                        username_status.append(" ");
                        username_status.append(first_icon);
                        username_status.append(" ");
                        username_status.append(second_icon);
                    }
                    //-----
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopWebMobile(clientStatuses)) {

                        View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent();
                        SimpleDraweeSpanTextView username_status = parent.findViewById(Utils.getResId("username_text", "id"));

                        DraweeSpanStringBuilder first_icon = new DraweeSpanStringBuilder();
                        first_icon.append(" ", new ImageSpan(isDesktop,1), 0);

                        DraweeSpanStringBuilder second_icon = new DraweeSpanStringBuilder();
                        second_icon.append(" ", new ImageSpan(isWeb,1), 0);

                        DraweeSpanStringBuilder third_icon = new DraweeSpanStringBuilder();
                        third_icon.append(" ", new ImageSpan(isMobile,1), 0);

                        username_status.append(" ");
                        username_status.append(first_icon);
                        username_status.append(" ");
                        username_status.append(second_icon);
                        username_status.append(" ");
                        username_status.append(third_icon);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopWebMobileDND(clientStatuses)) {

                        View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent();
                        SimpleDraweeSpanTextView username_status = parent.findViewById(Utils.getResId("username_text", "id"));

                        DraweeSpanStringBuilder first_icon = new DraweeSpanStringBuilder();
                        first_icon.append(" ", new ImageSpan(isDesktopDND,1), 0);

                        DraweeSpanStringBuilder second_icon = new DraweeSpanStringBuilder();
                        second_icon.append(" ", new ImageSpan(isWebDND,1), 0);

                        DraweeSpanStringBuilder third_icon = new DraweeSpanStringBuilder();
                        third_icon.append(" ", new ImageSpan(isMobileDND,1), 0);

                        username_status.append(" ");
                        username_status.append(first_icon);
                        username_status.append(" ");
                        username_status.append(second_icon);
                        username_status.append(" ");
                        username_status.append(third_icon);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopWebMobileIDLE(clientStatuses)) {

                        View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent();
                        SimpleDraweeSpanTextView username_status = parent.findViewById(Utils.getResId("username_text", "id"));

                        DraweeSpanStringBuilder first_icon = new DraweeSpanStringBuilder();
                        first_icon.append(" ", new ImageSpan(isDesktopIDLE,1), 0);

                        DraweeSpanStringBuilder second_icon = new DraweeSpanStringBuilder();
                        second_icon.append(" ", new ImageSpan(isWebIDLE,1), 0);

                        DraweeSpanStringBuilder third_icon = new DraweeSpanStringBuilder();
                        third_icon.append(" ", new ImageSpan(isMobileIDLE,1), 0);

                        username_status.append(" ");
                        username_status.append(first_icon);
                        username_status.append(" ");
                        username_status.append(second_icon);
                        username_status.append(" ");
                        username_status.append(third_icon);
                    }
                    //-----
                    if (imageResourceD == null) return;
                }
            }));

            //--------UserProfileHeaderView---------

            try {
                patcher.patch(UserProfileHeaderView.class.getDeclaredMethod("updateViewState", UserProfileHeaderViewModel.ViewState.Loaded.class), new PinePatchFn(callFrame1 -> {
                    try {

                        Presence presence1 = ((UserProfileHeaderViewModel.ViewState.Loaded) callFrame1.args[0]).getPresence();
                        if (presence1 == null) return;
                        ClientStatuses clientStatuses1 = presence1.getClientStatuses();
                        if (presence1 != null) {
                            if (clientStatuses1 != null && PresenceUtils.INSTANCE.isWeb(clientStatuses1)) {
                                View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                TextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));

                                username_profile_header2.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                            }
                            if (clientStatuses1 != null && PresenceUtils.INSTANCE.isWebDND(clientStatuses1)) {
                                View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                TextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));

                                username_profile_header2.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                            }
                            if (clientStatuses1 != null && PresenceUtils.INSTANCE.isWebIDLE(clientStatuses1)) {
                                View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                TextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));

                                username_profile_header2.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                            }
                            //------
                            if (clientStatuses1 != null && PresenceUtils.INSTANCE.isDesktop(clientStatuses1)) {
                                View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                TextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));

                                username_profile_header2.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                            }
                            if (clientStatuses1 != null && PresenceUtils.INSTANCE.isDesktopDND(clientStatuses1)) {
                                View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                TextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));

                                username_profile_header2.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                            }
                            if (clientStatuses1 != null && PresenceUtils.INSTANCE.isDesktopIDLE(clientStatuses1)) {
                                View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                TextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));

                                username_profile_header2.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                            }
                            //-------
                            if (clientStatuses1 != null && PresenceUtils.INSTANCE.isMobile(clientStatuses1)) {
                                View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                TextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));

                                username_profile_header2.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                            }
                            if (clientStatuses1 != null && PresenceUtils.INSTANCE.isMobileDND(clientStatuses1)) {
                                View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                TextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));

                                username_profile_header2.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                            }
                            if (clientStatuses1 != null && PresenceUtils.INSTANCE.isMobileIDLE(clientStatuses1)) {
                                View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                TextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));

                                username_profile_header2.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                            }
                            //-------
                            if (clientStatuses1 != null && PresenceUtils.INSTANCE.isWebMobile(clientStatuses1)) {
                                View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                TextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));

                                username_profile_header2.setCompoundDrawablesWithIntrinsicBounds(null, null, isWebMobile, null);
                                username_profile_header2.setCompoundDrawablePadding(2);
                            }
                            if (clientStatuses1 != null && PresenceUtils.INSTANCE.isWebMobileDND(clientStatuses1)) {
                                View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                TextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));

                                username_profile_header2.setCompoundDrawablesWithIntrinsicBounds(null, null, isWebMobileDND, null);
                                username_profile_header2.setCompoundDrawablePadding(2);
                            }
                            if (clientStatuses1 != null && PresenceUtils.INSTANCE.isWebMobileIDLE(clientStatuses1)) {
                                View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                TextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));

                                username_profile_header2.setCompoundDrawablesWithIntrinsicBounds(null, null, isWebMobileIDLE, null);
                                username_profile_header2.setCompoundDrawablePadding(2);
                            }
                            //-------------------
                            if (clientStatuses1 != null && PresenceUtils.INSTANCE.isDesktopAndMobile(clientStatuses1)) {
                                View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                TextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));

                                username_profile_header2.setCompoundDrawablesWithIntrinsicBounds(null, null, isDesktopMobile, null);
                                username_profile_header2.setCompoundDrawablePadding(2);
                            }
                            if (clientStatuses1 != null && PresenceUtils.INSTANCE.isDesktopAndMobileDND(clientStatuses1)) {
                                View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                TextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));

                                username_profile_header2.setCompoundDrawablesWithIntrinsicBounds(null, null, isDesktopMobileDND, null);
                                username_profile_header2.setCompoundDrawablePadding(2);
                            }
                            if (clientStatuses1 != null && PresenceUtils.INSTANCE.isDesktopAndMobileIDLE(clientStatuses1)) {
                                View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                TextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));

                                username_profile_header2.setCompoundDrawablesWithIntrinsicBounds(null, null, isDesktopMobileIDLE, null);
                                username_profile_header2.setCompoundDrawablePadding(2);
                            }
                            //-------
                            if (clientStatuses1 != null && PresenceUtils.INSTANCE.isDesktopAndWeb(clientStatuses1)) {
                                View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                TextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));

                                username_profile_header2.setCompoundDrawablesWithIntrinsicBounds(null, null, isDesktopWeb, null);
                                username_profile_header2.setCompoundDrawablePadding(2);
                            }
                            if (clientStatuses1 != null && PresenceUtils.INSTANCE.isDesktopAndWebDND(clientStatuses1)) {
                                View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                TextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));

                                username_profile_header2.setCompoundDrawablesWithIntrinsicBounds(null, null, isDesktopWebDND, null);
                                username_profile_header2.setCompoundDrawablePadding(2);
                            }
                            if (clientStatuses1 != null && PresenceUtils.INSTANCE.isDesktopAndWebIDLE(clientStatuses1)) {
                                View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                TextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));

                                username_profile_header2.setCompoundDrawablesWithIntrinsicBounds(null, null, isDesktopWebIDLE, null);
                                username_profile_header2.setCompoundDrawablePadding(2);
                            }
                            //-------
                            if (clientStatuses1 != null && PresenceUtils.INSTANCE.isDesktopWebMobile(clientStatuses1)) {
                                View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                TextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));

                                username_profile_header2.setCompoundDrawablesWithIntrinsicBounds(null, null, isDesktopWebMobile, null);
                                username_profile_header2.setCompoundDrawablePadding(2);
                            }
                            if (clientStatuses1 != null && PresenceUtils.INSTANCE.isDesktopWebMobileDND(clientStatuses1)) {
                                View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                TextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));

                                username_profile_header2.setCompoundDrawablesWithIntrinsicBounds(null, null, isDesktopWebMobileDND, null);
                                username_profile_header2.setCompoundDrawablePadding(2);
                            }
                            if (clientStatuses1 != null && PresenceUtils.INSTANCE.isDesktopWebMobileIDLE(clientStatuses1)) {
                                View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                TextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));

                                username_profile_header2.setCompoundDrawablesWithIntrinsicBounds(null, null, isDesktopWebMobileIDLE, null);
                                username_profile_header2.setCompoundDrawablePadding(2);
                            }
                        }
                    } catch (Throwable e) {
                        logger.error("An error occurred in UserProfileHeaderView", e);
                    }
                }));
            } catch (Throwable e) {
                logger.error("An error occurred in UserProfileHeaderView", e);
            }

        } else {
            //if (settings.getBool("filled_colors", true) == true)
            patcher.patch(StatusView.class.getDeclaredMethod("setPresence", Presence.class), new PinePatchFn(callFrame -> {
                //patcher.patch(StatusView.class.getDeclaredMethod("setPresence", Presence.class), (MethodHook) ((settings.getBool("filled_colors", false)) ? new PinePatchFn(callFrame -> {
                Drawable imageResourceD = null;

                int imageResource = 0;
                Presence presence = (Presence) callFrame.args[0];
                if (presence == null) return;
                ClientStatuses clientStatuses = presence.getClientStatuses();
                //int id = AppCompatImageView.generateViewId();

                if (presence != null) {
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isWeb(clientStatuses)) {
                        imageResourceD = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_online", "drawable", "com.aliucord.plugins"), null);
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isWebDND(clientStatuses)) {
                        imageResourceD = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_dnd", "drawable", "com.aliucord.plugins"), null);
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isWebIDLE(clientStatuses)) {
                        imageResourceD = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_idle", "drawable", "com.aliucord.plugins"), null);
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                    }
                    //-----
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktop(clientStatuses)) {
                        imageResourceD = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_online", "drawable", "com.aliucord.plugins"), null);
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopDND(clientStatuses)) {
                        imageResourceD = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_dnd", "drawable", "com.aliucord.plugins"), null);
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopIDLE(clientStatuses)) {
                        imageResourceD = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_idle", "drawable", "com.aliucord.plugins"), null);
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                    }
                    //-----
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isMobile(clientStatuses)) {
                        imageResourceD = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_mobile", "drawable", "com.aliucord.plugins"), null);
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isMobileDND(clientStatuses)) {
                        imageResourceD = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_dnd", "drawable", "com.aliucord.plugins"), null);
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isMobileIDLE(clientStatuses)) {
                        imageResourceD = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_idle", "drawable", "com.aliucord.plugins"), null);
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                    }
                    //-----
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isWebMobile(clientStatuses)) {
                        imageResourceD = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_online", "drawable", "com.aliucord.plugins"), null);
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isWebMobileDND(clientStatuses)) {
                        imageResourceD = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_dnd", "drawable", "com.aliucord.plugins"), null);
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isWebMobileIDLE(clientStatuses)) {
                        imageResourceD = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_idle", "drawable", "com.aliucord.plugins"), null);
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                    }
                    //-----
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndMobile(clientStatuses)) {
                        imageResourceD = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_online", "drawable", "com.aliucord.plugins"), null);
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndMobileDND(clientStatuses)) {
                        imageResourceD = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_dnd", "drawable", "com.aliucord.plugins"), null);
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndMobileIDLE(clientStatuses)) {
                        imageResourceD = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_idle", "drawable", "com.aliucord.plugins"), null);
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                    }
                    //-----
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndWeb(clientStatuses)) {
                        imageResourceD = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_online", "drawable", "com.aliucord.plugins"), null);
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndWebDND(clientStatuses)) {
                        imageResourceD = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_dnd", "drawable", "com.aliucord.plugins"), null);
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndWebIDLE(clientStatuses)) {
                        imageResourceD = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_idle", "drawable", "com.aliucord.plugins"), null);
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                    }
                    //-----
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopWebMobile(clientStatuses)) {
                        imageResourceD = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_online", "drawable", "com.aliucord.plugins"), null);
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopWebMobileDND(clientStatuses)) {
                        imageResourceD = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_dnd", "drawable", "com.aliucord.plugins"), null);
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopWebMobileIDLE(clientStatuses)) {
                        imageResourceD = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_idle", "drawable", "com.aliucord.plugins"), null);
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                    }
                    //-----
                    if (imageResourceD == null) return;

                }
            }));
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
        }
    }
}
