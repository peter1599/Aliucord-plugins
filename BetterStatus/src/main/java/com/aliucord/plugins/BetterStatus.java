package com.aliucord.plugins;

import android.annotation.SuppressLint;
import android.content.Context;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.VectorDrawable;
import android.graphics.drawable.shapes.OvalShape;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import com.aliucord.Logger;
import com.aliucord.PluginManager;
import com.aliucord.Utils;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.api.SettingsAPI;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.InsteadHook;
import com.aliucord.patcher.PinePrePatchFn;
import com.aliucord.utils.DimenUtils;
import com.aliucord.widgets.BottomSheet;
import com.discord.api.presence.ClientStatus;
import com.discord.api.presence.ClientStatuses;
import com.aliucord.plugins.betterstatus.PresenceUtils;
import com.aliucord.patcher.PinePatchFn;
import com.discord.app.App;
import com.discord.databinding.UserProfileHeaderViewBinding;
import com.discord.databinding.WidgetChannelMembersListItemUserBinding;
import com.discord.databinding.WidgetChannelsListItemChannelPrivateBinding;
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
import com.discord.utilities.icon.IconUtils;
import com.discord.utilities.images.MGImages;
import com.discord.views.CheckedSetting;
import com.discord.views.StatusView;
import com.discord.views.UsernameView;
import com.discord.views.user.UserAvatarPresenceView;
import com.discord.views.user.UserAvatarPresenceView$b;
import com.discord.widgets.channels.list.WidgetChannelsList$binding$2;
import com.discord.widgets.channels.list.WidgetChannelsListAdapter;
import com.discord.widgets.channels.list.items.ChannelListItem;
import com.discord.widgets.channels.list.items.ChannelListItemPrivate;
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
import com.facebook.drawee.view.SimpleDraweeView;

import androidx.appcompat.widget.AppCompatImageView;

import java.lang.reflect.Field;
import java.util.*;

import c.a.i.s1;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import top.canyie.pine.callback.MethodHook;

@SuppressWarnings("unused")
@AliucordPlugin
public class BetterStatus extends Plugin {

    public BetterStatus() {
        needsResources = true;
        settingsTab = new SettingsTab(BetterStatusSettings.class, SettingsTab.Type.BOTTOM_SHEET).withArgs(settings);
    }
    public Logger logger = new Logger("BetterStatus");

    /*@NonNull
    @Override
    public Manifest getManifest() {
        Manifest manifest = new Manifest();
        manifest.authors = new Manifest.Author[]{ new Manifest.Author("Butterfly3ffect", 575606699553980430L) };
        manifest.description = "Changes Discord default status icons (online,idle etc..) to better ones.";
        manifest.version = "1.0.0";
        manifest.updateUrl = "https://raw.githubusercontent.com/peter1599/Aliucord-plugins/builds/updater.json";
        return manifest;
    }*/

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
        //appCompatImageView.setPadding(2, 2, 2, 2);
        //AppCompatImageView status = appCompatImageView.findViewById(0x7F0A01C9);
        //appCompatImageView.getLayoutParams().width = width;
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

    /*public void getClients(long userId) {
        //const isSelf = userId == AuthStore.getId();
        MeUser meUser;
        //User user;
        ClientStatuses status;
        Presence presence;

        long isMe = meUser.getId();
        boolean isSelf = User.getId() == isMe;
        //const status = isSelf ? currentClientStatus : UserStatusStore.getState().clientStatuses[userId];
        status = isSelf ? presence.getClientStatuses() : StoreUser
        //return status !== null && status !== void 0 ? status : {};
    }*/

    /*public boolean getClients(long userId) {
        User user;
        ClientStatus currentClientStatus;
        ClientStatuses clientStatuses;

        var isSelf = userId == user.getId();
        var status = isSelf ? currentClientStatus : clientStatuses.a();
        return status != null;
                //&& status != void 0 ? status : {};
    }*/

    /*@SuppressLint("ResourceType")
    public void setUsernameStatus(SpannableStringBuilder spannableStringBuilder, int imageResource, int width)
    {
        spannableStringBuilder.setSpan(new ImageSpan(imageResource));
        appCompatImageView.setAdjustViewBounds(true);
        AppCompatImageView status = appCompatImageView.findViewById(0x7F0A01C9);
        status.getLayoutParams().width = width;
    }*/

    /*@SuppressLint("ResourceType")
    public void setImageDrawableWidth(AppCompatImageView appCompatImageView, int width)
    {
        AppCompatImageView status = appCompatImageView.findViewById(0x7F0A01C9);
        status.getLayoutParams().width = width;
    }*/

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
        //Field bindingField = ChannelMembersListViewHolderMember.class.getDeclaredField("binding");
        //bindingField.setAccessible(true);

        //var bindingField = WidgetChatListAdapterItemMessage.class.getDeclaredField("")

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


        /*patcher.patch(ChannelMembersListViewHolderMember.class, "bind", new Class<?>[]{ ChannelMembersListAdapter.Item.Member.class, Function0.class}, new PinePatchFn(callFrame -> {
            try {
                WidgetChannelMembersListItemUserBinding binding = (WidgetChannelMembersListItemUserBinding) bindingField.get(callFrame.thisObject);
                ConstraintLayout layout = (ConstraintLayout) binding.getRoot();
                ChannelMembersListAdapter.Item.Member user = (ChannelMembersListAdapter.Item.Member) callFrame.args[0];
                User user1 = null;
                //String tag = settings.getString(String.valueOf(user.getUserId()), null);
                //boolean verified = settings.getBool(user.getUserId() + "_verified", false);

                if(user.isBot() == false) {
                    TextView tagText = (TextView) layout.findViewById(Utils.getResId("username_tag", "id"));
                    tagText.setText(String.valueOf(tag));
                    //if(user.getUserId() == 298295889720770563L || verified == true) {
                        tagText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_mobile, 0, 0, 0);
                    //}
                    tagText.setVisibility(View.VISIBLE);
                }

                if(user.getUserId() == user1.getId()) {
                    TextView tagText = (TextView) layout.findViewById(Utils.getResId("username_text", "id"));
                    //if(user.getUserId() == 298295889720770563L || verified == true) {
                    tagText.setCompoundDrawables(isMobile, null, null, null);
                    //}
                    tagText.setVisibility(View.VISIBLE);
                }
            } catch(Throwable e) {
                e.printStackTrace();
            }
        }));*/

        /*patcher.patch(WidgetChatListAdapterItemMessage.class.getDeclaredMethod("onConfigure", int.class, ChatListEntry.class), new PinePatchFn(callFrame -> {
            //StatusView chatStatus;
            //AppCompatImageView chatStatus = new AppCompatImageView((Context) callFrame.thisObject);
            //WidgetChatListAdapterItemMessage
            //StatusView statusView = new StatusView()
            ///final StatusView h;
        }));*/


        /*patcher.patch(Chann.class.getDeclaredMethod("", SimpleDraweeSpanTextView.class, MessageEntry.class), new PinePatchFn(callFrame -> {
            //StatusView chatStatus;
            //AppCompatImageView chatStatus = new AppCompatImageView((Context) callFrame.thisObject);
            //WidgetChatListAdapterItemMessage
            //StatusView statusView = new StatusView()
            ///final StatusView h;
            com.discord.models.user.User user = null;


            MessageEntry message = (MessageEntry) callFrame.args[1];
            if(message.getAuthor().getUserId() == user.getId()){

            }
        }));*/

        if (settings.getBool("filled_colors", false) == false) {

            //patcher.patch(StatusView.class.getDeclaredMethod("setPresence", Presence.class), new PinePatchFn(callFrame -> {
            patcher.patch(StatusView.class.getDeclaredMethod("setPresence", Presence.class), new PinePatchFn(callFrame -> {
                //patcher.patch(StatusView.class.getDeclaredMethod("setPresence", Presence.class), (MethodHook) ((settings.getBool("filled_colors", false)) ? new PinePatchFn(callFrame -> {
                Drawable imageResourceD = null;

                int imageResource = 0;
                Presence presence = (Presence) callFrame.args[0];
                //logger.info(String.valueOf(presence));
                if (presence == null) return;
                ClientStatuses clientStatuses = presence.getClientStatuses();
                //int id = AppCompatImageView.generateViewId();

                if (presence != null) {
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isWeb(clientStatuses)) {
                        imageResourceD = isWeb;
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                            /*try {
                                View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                                TextView username_status = parent.findViewById(Utils.getResId("username_text", "id"));
                                //username_status.setCompoundDrawablePadding(1);
                                username_status.setCompoundDrawables(null, null,null,null);
                                patcher.patch(UserProfileHeaderView.class.getDeclaredMethod("configurePrimaryName", UserProfileHeaderViewModel.ViewState.Loaded.class), new PinePatchFn(callFrame1 -> {
                                    try {
                                        //View parent2 = (View) ((LinearLayout) callFrame1.thisObject).getParent(); //UserProfileHeaderView class or method?

                                        //View username_profile_header = UserProfileHeaderView.access$getBinding$p((UserProfileHeaderView) callFrame1.thisObject).getRoot();
                                        View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                        SimpleDraweeSpanTextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));
                                        //username_status.setCompoundDrawablePadding(1);
                                        username_profile_header2.setCompoundDrawables(null, null,null,null);
                                    } catch (Throwable e) {
                                        logger.error("Error setting mobile for userprofileheader", e);
                                    }
                                }));
                            } catch (Throwable e) {
                                logger.error("Error setting null when web", e);
                            }*/
                        //setImageDrawableWidth((AppCompatImageView) _this, 35);
                        //setImageDrawableWidth2((AppCompatImageView) _this, 35);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isWebDND(clientStatuses)) {
                        imageResourceD = isWebDND;
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                            /*try {
                                View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                                TextView username_status = parent.findViewById(Utils.getResId("username_text", "id"));
                                //username_status.setCompoundDrawablePadding(1);
                                username_status.setCompoundDrawables(null, null,null,null);
                                patcher.patch(UserProfileHeaderView.class.getDeclaredMethod("configurePrimaryName", UserProfileHeaderViewModel.ViewState.Loaded.class), new PinePatchFn(callFrame1 -> {
                                    try {
                                        //View parent2 = (View) ((LinearLayout) callFrame1.thisObject).getParent(); //UserProfileHeaderView class or method?

                                        //View username_profile_header = UserProfileHeaderView.access$getBinding$p((UserProfileHeaderView) callFrame1.thisObject).getRoot();
                                        View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                        SimpleDraweeSpanTextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));
                                        //username_status.setCompoundDrawablePadding(1);
                                        username_profile_header2.setCompoundDrawables(null, null,null,null);
                                    } catch (Throwable e) {
                                        logger.error("Error setting mobile for userprofileheader", e);
                                    }
                                }));
                            } catch (Throwable e) {
                                logger.error("Error setting null when web_dnd", e);
                            }*/
                        //setImageDrawableWidth((AppCompatImageView) _this, 35);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isWebIDLE(clientStatuses)) {
                        imageResourceD = isWebIDLE;
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                            /*try {
                                View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                                TextView username_status = parent.findViewById(Utils.getResId("username_text", "id"));
                                //username_status.setCompoundDrawablePadding(1);
                                username_status.setCompoundDrawables(null, null,null,null);
                                patcher.patch(UserProfileHeaderView.class.getDeclaredMethod("configurePrimaryName", UserProfileHeaderViewModel.ViewState.Loaded.class), new PinePatchFn(callFrame1 -> {
                                    try {
                                        //View parent2 = (View) ((LinearLayout) callFrame1.thisObject).getParent(); //UserProfileHeaderView class or method?

                                        //View username_profile_header = UserProfileHeaderView.access$getBinding$p((UserProfileHeaderView) callFrame1.thisObject).getRoot();
                                        View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                        SimpleDraweeSpanTextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));
                                        //username_status.setCompoundDrawablePadding(1);
                                        username_profile_header2.setCompoundDrawables(null, null,null,null);
                                    } catch (Throwable e) {
                                        logger.error("Error setting mobile for userprofileheader", e);
                                    }
                                }));
                            } catch (Throwable e) {
                                logger.error("Error setting null when web_idle", e);
                            }*/
                        //setImageDrawableWidth((AppCompatImageView) _this, 35);
                    }
                    //-----
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktop(clientStatuses)) {
                        imageResourceD = isDesktop;
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                            /*try {
                                View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                                TextView username_status = parent.findViewById(Utils.getResId("username_text", "id"));
                                //username_status.setCompoundDrawablePadding(1);
                                username_status.setCompoundDrawables(null, null,null,null);
                                patcher.patch(UserProfileHeaderView.class.getDeclaredMethod("configurePrimaryName", UserProfileHeaderViewModel.ViewState.Loaded.class), new PinePatchFn(callFrame1 -> {
                                    try {
                                        //View parent2 = (View) ((LinearLayout) callFrame1.thisObject).getParent(); //UserProfileHeaderView class or method?

                                        //View username_profile_header = UserProfileHeaderView.access$getBinding$p((UserProfileHeaderView) callFrame1.thisObject).getRoot();
                                        View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                        SimpleDraweeSpanTextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));
                                        //username_status.setCompoundDrawablePadding(1);
                                        username_profile_header2.setCompoundDrawables(null, null,null,null);
                                    } catch (Throwable e) {
                                        logger.error("Error setting mobile for userprofileheader", e);
                                    }
                                }));
                            } catch (Throwable e) {
                                logger.error("Error setting null when desktop", e);
                            }*/
                        //setImageDrawableWidth((AppCompatImageView) _this, 35);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopDND(clientStatuses)) {
                        imageResourceD = isDesktopDND;
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                            /*try {
                                View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                                TextView username_status = parent.findViewById(Utils.getResId("username_text", "id"));
                                //username_status.setCompoundDrawablePadding(1);
                                username_status.setCompoundDrawables(null, null,null,null);
                                patcher.patch(UserProfileHeaderView.class.getDeclaredMethod("configurePrimaryName", UserProfileHeaderViewModel.ViewState.Loaded.class), new PinePatchFn(callFrame1 -> {
                                    try {
                                        //View parent2 = (View) ((LinearLayout) callFrame1.thisObject).getParent(); //UserProfileHeaderView class or method?

                                        //View username_profile_header = UserProfileHeaderView.access$getBinding$p((UserProfileHeaderView) callFrame1.thisObject).getRoot();
                                        View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                        SimpleDraweeSpanTextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));
                                        //username_status.setCompoundDrawablePadding(1);
                                        username_profile_header2.setCompoundDrawables(null, null,null,null);
                                    } catch (Throwable e) {
                                        logger.error("Error setting mobile for userprofileheader", e);
                                    }
                                }));
                            } catch (Throwable e) {
                                logger.error("Error setting null when desktop_dnd", e);
                            }*/
                        //setImageDrawableWidth((AppCompatImageView) _this, 35);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopIDLE(clientStatuses)) {
                        imageResourceD = isDesktopIDLE;
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                            /*try {
                                View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                                TextView username_status = parent.findViewById(Utils.getResId("username_text", "id"));
                                //username_status.setCompoundDrawablePadding(1);
                                username_status.setCompoundDrawables(null, null,null,null);
                                patcher.patch(UserProfileHeaderView.class.getDeclaredMethod("configurePrimaryName", UserProfileHeaderViewModel.ViewState.Loaded.class), new PinePatchFn(callFrame1 -> {
                                    try {
                                        //View parent2 = (View) ((LinearLayout) callFrame1.thisObject).getParent(); //UserProfileHeaderView class or method?

                                        //View username_profile_header = UserProfileHeaderView.access$getBinding$p((UserProfileHeaderView) callFrame1.thisObject).getRoot();
                                        View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                        SimpleDraweeSpanTextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));
                                        //username_status.setCompoundDrawablePadding(1);
                                        username_profile_header2.setCompoundDrawables(null, null,null,null);
                                    } catch (Throwable e) {
                                        logger.error("Error setting mobile for userprofileheader", e);
                                    }
                                }));
                            } catch (Throwable e) {
                                logger.error("Error setting null when desktop_idle", e);
                            }*/
                        //setImageDrawableWidth((AppCompatImageView) _this, 35);
                    }
                    //-----
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isMobile(clientStatuses)) {
                        imageResourceD = isMobile;
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);


                            /*try {
                                View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                                TextView username_status = parent.findViewById(Utils.getResId("username_text", "id"));
                                //username_status.setCompoundDrawablePadding(1);
                                username_status.setCompoundDrawables(null, null, null, null);
                                patcher.patch(UserProfileHeaderView.class.getDeclaredMethod("configurePrimaryName", UserProfileHeaderViewModel.ViewState.Loaded.class), new PinePatchFn(callFrame1 -> {
                                    try {
                                        //View parent2 = (View) ((LinearLayout) callFrame1.thisObject).getParent(); //UserProfileHeaderView class or method?

                                        //View username_profile_header = UserProfileHeaderView.access$getBinding$p((UserProfileHeaderView) callFrame1.thisObject).getRoot();
                                        View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                        SimpleDraweeSpanTextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));
                                        //username_status.setCompoundDrawablePadding(1);
                                        username_profile_header2.setCompoundDrawables(null, null,null,null);
                                    } catch (Throwable e) {
                                        logger.error("Error setting mobile for userprofileheader", e);
                                    }
                                }));
                            } catch (Throwable e) {
                                logger.error("Error setting null when mobile", e);
                            }*/
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isMobileDND(clientStatuses)) {
                        imageResourceD = isMobileDND;
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                            /*try {
                                View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                                TextView username_status = parent.findViewById(Utils.getResId("username_text", "id"));
                                //username_status.setCompoundDrawablePadding(1);
                                username_status.setCompoundDrawables(null, null,null,null);
                                patcher.patch(UserProfileHeaderView.class.getDeclaredMethod("configurePrimaryName", UserProfileHeaderViewModel.ViewState.Loaded.class), new PinePatchFn(callFrame1 -> {
                                    try {
                                        //View parent2 = (View) ((LinearLayout) callFrame1.thisObject).getParent(); //UserProfileHeaderView class or method?

                                        //View username_profile_header = UserProfileHeaderView.access$getBinding$p((UserProfileHeaderView) callFrame1.thisObject).getRoot();
                                        View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                        SimpleDraweeSpanTextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));
                                        //username_status.setCompoundDrawablePadding(1);
                                        username_profile_header2.setCompoundDrawables(null, null,null,null);
                                    } catch (Throwable e) {
                                        logger.error("Error setting mobile for userprofileheader", e);
                                    }
                                }));
                            } catch (Throwable e) {
                                logger.error("Error setting null when mobile_dnd", e);
                            }*/
                        //setImageDrawableWidth((AppCompatImageView) _this, 40);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isMobileIDLE(clientStatuses)) {
                        imageResourceD = isMobileIDLE;
                        setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);

                            /*try {
                                View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                                TextView username_status = parent.findViewById(Utils.getResId("username_text", "id"));
                                //username_status.setCompoundDrawablePadding(1);
                                username_status.setCompoundDrawables(null, null,null,null);
                                patcher.patch(UserProfileHeaderView.class.getDeclaredMethod("configurePrimaryName", UserProfileHeaderViewModel.ViewState.Loaded.class), new PinePatchFn(callFrame1 -> {
                                    try {
                                        //View parent2 = (View) ((LinearLayout) callFrame1.thisObject).getParent(); //UserProfileHeaderView class or method?

                                        //View username_profile_header = UserProfileHeaderView.access$getBinding$p((UserProfileHeaderView) callFrame1.thisObject).getRoot();
                                        View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                        SimpleDraweeSpanTextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));
                                        //username_status.setCompoundDrawablePadding(1);
                                        username_profile_header2.setCompoundDrawables(null, null,null,null);
                                    } catch (Throwable e) {
                                        logger.error("Error setting mobile for userprofileheader", e);
                                    }
                                }));
                            } catch (Throwable e) {
                                logger.error("Error setting null when mobile_idle", e);
                            }*/
                        //setImageDrawableWidth((AppCompatImageView) _this, 40);
                    }
                    //-----
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isWebMobile(clientStatuses)) {
                        //imageResourceD = isWebMobile;
                        //imageResourceD = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_online", "drawable", "com.aliucord.plugins"), null);
                        //imageResourceD = isWeb;
                        //setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);

                        View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent();
                        //View parent2 = (View) ((AppCompatImageView) callFrame.thisObject).getRootView();
                        //TextView username = TUtils.getResId("username_text", "id");
                        SimpleDraweeSpanTextView username_status = parent.findViewById(Utils.getResId("username_text", "id"));

                        DraweeSpanStringBuilder first_icon = new DraweeSpanStringBuilder();
                        first_icon.append(" ", new ImageSpan(isWeb,1), 0);

                        DraweeSpanStringBuilder second_icon = new DraweeSpanStringBuilder();
                        second_icon.append(" ", new ImageSpan(isMobile,1), 0);

                        username_status.append(" ");
                        username_status.append(first_icon);
                        username_status.append(" ");
                        username_status.append(second_icon);

                        //TextView username = ((TextView) callFrame.thisObject).findViewById(0x7f0a0209);
                        //username.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_mobile, 0, 0, 0);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isWebMobileDND(clientStatuses)) {
                        //imageResourceD = isWebMobileDND;
                        //setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);

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
                            /*try {
                                View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                                TextView username_status = parent.findViewById(Utils.getResId("username_text", "id"));
                                username_status.setCompoundDrawables(isMobileDND, null,null,null);
                                username_status.setCompoundDrawablePadding(1);
                                patcher.patch(UserProfileHeaderView.class.getDeclaredMethod("configurePrimaryName", UserProfileHeaderViewModel.ViewState.Loaded.class), new PinePatchFn(callFrame1 -> {
                                    try {
                                        //View parent2 = (View) ((LinearLayout) callFrame1.thisObject).getParent(); //UserProfileHeaderView class or method?

                                        //View username_profile_header = UserProfileHeaderView.access$getBinding$p((UserProfileHeaderView) callFrame1.thisObject).getRoot();
                                        View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                        SimpleDraweeSpanTextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));
                                        //username_status.setCompoundDrawablePadding(1);
                                        username_profile_header2.setCompoundDrawables(isMobileDND, null,null,null);
                                    } catch (Throwable e) {
                                        logger.error("Error setting mobile for userprofileheader", e);
                                    }
                                }));
                            } catch (Throwable e) {
                                logger.error("Error setting ic_mobile_dnd", e);
                            }*/
                        //setImageDrawableWidth((ImageView) _this, 48);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isWebMobileIDLE(clientStatuses)) {
                        //imageResourceD = isWebMobileIDLE;
                        //setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);

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
                            /*try {
                                View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                                TextView username_status = parent.findViewById(Utils.getResId("username_text", "id"));
                                username_status.setCompoundDrawables(isMobileIDLE, null,null,null);
                                username_status.setCompoundDrawablePadding(1);
                                patcher.patch(UserProfileHeaderView.class.getDeclaredMethod("configurePrimaryName", UserProfileHeaderViewModel.ViewState.Loaded.class), new PinePatchFn(callFrame1 -> {
                                    try {
                                        //View parent2 = (View) ((LinearLayout) callFrame1.thisObject).getParent(); //UserProfileHeaderView class or method?

                                        //View username_profile_header = UserProfileHeaderView.access$getBinding$p((UserProfileHeaderView) callFrame1.thisObject).getRoot();
                                        View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                        SimpleDraweeSpanTextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));
                                        //username_status.setCompoundDrawablePadding(1);
                                        username_profile_header2.setCompoundDrawables(isMobileIDLE, null,null,null);
                                    } catch (Throwable e) {
                                        logger.error("Error setting mobile for userprofileheader", e);
                                    }
                                }));
                            } catch (Throwable e) {
                                logger.error("Error setting ic_mobile_idle", e);
                            }*/
                        //setImageDrawableWidth((ImageView) _this, 48);
                    }
                    //-----
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndMobile(clientStatuses)) {
                        //imageResourceD = isDesktopMobile;
                        //setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);

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
                            /*try {
                                View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                                TextView username_status = parent.findViewById(Utils.getResId("username_text", "id"));
                                username_status.setCompoundDrawables(isMobile, null,null,null);
                                username_status.setCompoundDrawablePadding(1);
                                patcher.patch(UserProfileHeaderView.class.getDeclaredMethod("configurePrimaryName", UserProfileHeaderViewModel.ViewState.Loaded.class), new PinePatchFn(callFrame1 -> {
                                    try {
                                        //View parent2 = (View) ((LinearLayout) callFrame1.thisObject).getParent(); //UserProfileHeaderView class or method?

                                        //View username_profile_header = UserProfileHeaderView.access$getBinding$p((UserProfileHeaderView) callFrame1.thisObject).getRoot();
                                        View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                        SimpleDraweeSpanTextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));
                                        //username_status.setCompoundDrawablePadding(1);
                                        username_profile_header2.setCompoundDrawables(isMobile, null,null,null);
                                    } catch (Throwable e) {
                                        logger.error("Error setting mobile for userprofileheader", e);
                                    }
                                }));
                            } catch (Throwable e) {
                                logger.error("Error setting ic_mobile", e);
                            }*/
                        //setImageDrawableWidth((ImageView) _this, 48);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndMobileDND(clientStatuses)) {
                        //imageResourceD = isDesktopMobileDND;
                        //setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);

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
                            /*try {
                                View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                                TextView username_status = parent.findViewById(Utils.getResId("username_text", "id"));
                                //username_status.setCompoundDrawablePadding(1);
                                //isMobileDND.setBounds(0, 0, isMobile.getIntrinsicWidth(), isMobile.getIntrinsicHeight());
                                username_status.setCompoundDrawables(isMobileDND, null,null,null);
                                username_status.setCompoundDrawablePadding(1);
                                patcher.patch(UserProfileHeaderView.class.getDeclaredMethod("configurePrimaryName", UserProfileHeaderViewModel.ViewState.Loaded.class), new PinePatchFn(callFrame1 -> {
                                    try {
                                        //View parent2 = (View) ((LinearLayout) callFrame1.thisObject).getParent(); //UserProfileHeaderView class or method?

                                        //View username_profile_header = UserProfileHeaderView.access$getBinding$p((UserProfileHeaderView) callFrame1.thisObject).getRoot();
                                        View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                        SimpleDraweeSpanTextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));
                                        //username_status.setCompoundDrawablePadding(1);
                                        username_profile_header2.setCompoundDrawables(isMobileDND, null,null,null);
                                    } catch (Throwable e) {
                                        logger.error("Error setting mobile for userprofileheader", e);
                                    }
                                }));
                            } catch (Throwable e) {
                                logger.error("Error setting ic_mobile_dnd", e);
                            }*/
                        //setImageDrawableWidth((ImageView) _this, 48);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndMobileIDLE(clientStatuses)) {
                        //imageResourceD = isDesktopMobileIDLE;
                        //setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);

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
                            /*try {
                                View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                                TextView username_status = parent.findViewById(Utils.getResId("username_text", "id"));
                                username_status.setCompoundDrawables(isMobileIDLE, null,null,null);
                                username_status.setCompoundDrawablePadding(1);
                                patcher.patch(UserProfileHeaderView.class.getDeclaredMethod("configurePrimaryName", UserProfileHeaderViewModel.ViewState.Loaded.class), new PinePatchFn(callFrame1 -> {
                                    try {
                                        //View parent2 = (View) ((LinearLayout) callFrame1.thisObject).getParent(); //UserProfileHeaderView class or method?

                                        //View username_profile_header = UserProfileHeaderView.access$getBinding$p((UserProfileHeaderView) callFrame1.thisObject).getRoot();
                                        View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                        SimpleDraweeSpanTextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));
                                        //username_status.setCompoundDrawablePadding(1);
                                        username_profile_header2.setCompoundDrawables(isMobileIDLE, null,null,null);
                                    } catch (Throwable e) {
                                        logger.error("Error setting mobile for userprofileheader", e);
                                    }
                                }));
                            } catch (Throwable e) {
                                logger.error("Error setting ic_mobile_idle", e);
                            }*/
                        //setImageDrawableWidth((ImageView) _this, 48);
                    }
                    //-----
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndWeb(clientStatuses)) {
                        //imageResourceD = isDesktopWeb;
                        //setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);

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
                            /*try {
                                View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                                TextView username_status = parent.findViewById(Utils.getResId("username_text", "id"));
                                username_status.setCompoundDrawables(isWeb, null,null,null);
                                username_status.setCompoundDrawablePadding(1);
                                patcher.patch(UserProfileHeaderView.class.getDeclaredMethod("configurePrimaryName", UserProfileHeaderViewModel.ViewState.Loaded.class), new PinePatchFn(callFrame1 -> {
                                    try {
                                        //View parent2 = (View) ((LinearLayout) callFrame1.thisObject).getParent(); //UserProfileHeaderView class or method?

                                        //View username_profile_header = UserProfileHeaderView.access$getBinding$p((UserProfileHeaderView) callFrame1.thisObject).getRoot();
                                        View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                        SimpleDraweeSpanTextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));
                                        //username_status.setCompoundDrawablePadding(1);
                                        username_profile_header2.setCompoundDrawables(isWeb, null,null,null);
                                    } catch (Throwable e) {
                                        logger.error("Error setting mobile for userprofileheader", e);
                                    }
                                }));
                            } catch (Throwable e) {
                                logger.error("Error setting ic_web", e);
                            }*/
                        //setImageDrawableWidth((ImageView) _this, 48);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndWebDND(clientStatuses)) {
                        //imageResourceD = isDesktopDND;
                        //setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);

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
                            /*try {
                                View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                                TextView username_status = parent.findViewById(Utils.getResId("username_text", "id"));
                                username_status.setCompoundDrawables(isWebDND, null,null,null);
                                username_status.setCompoundDrawablePadding(1);
                                patcher.patch(UserProfileHeaderView.class.getDeclaredMethod("configurePrimaryName", UserProfileHeaderViewModel.ViewState.Loaded.class), new PinePatchFn(callFrame1 -> {
                                    try {
                                        //View parent2 = (View) ((LinearLayout) callFrame1.thisObject).getParent(); //UserProfileHeaderView class or method?

                                        //View username_profile_header = UserProfileHeaderView.access$getBinding$p((UserProfileHeaderView) callFrame1.thisObject).getRoot();
                                        View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                        SimpleDraweeSpanTextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));
                                        //username_status.setCompoundDrawablePadding(1);
                                        username_profile_header2.setCompoundDrawables(isWebDND, null,null,null);
                                    } catch (Throwable e) {
                                        logger.error("Error setting mobile for userprofileheader", e);
                                    }
                                }));
                                //---

                                //TextView username_status2 = parent.findViewById(0x7f0a0f9b);
                                //username_status2.setCompoundDrawables(isWebDND, null,null,null);
                                //username_status.setCompoundDrawablePadding(1);
                            } catch (Throwable e) {
                                logger.error("Error setting ic_web_dnd_desktop", e);
                            }*/
                        //setImageDrawableWidth((ImageView) _this, 48);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndWebIDLE(clientStatuses)) {
                        //imageResourceD = isDesktopWebIDLE;
                        //setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);

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
                            /*try {
                                View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                                TextView username_status = parent.findViewById(Utils.getResId("username_text", "id"));
                                username_status.setCompoundDrawables(isWebIDLE, null,null,null);
                                username_status.setCompoundDrawablePadding(1);
                                patcher.patch(UserProfileHeaderView.class.getDeclaredMethod("configurePrimaryName", UserProfileHeaderViewModel.ViewState.Loaded.class), new PinePatchFn(callFrame1 -> {
                                    try {
                                        //View parent2 = (View) ((LinearLayout) callFrame1.thisObject).getParent(); //UserProfileHeaderView class or method?

                                        //View username_profile_header = UserProfileHeaderView.access$getBinding$p((UserProfileHeaderView) callFrame1.thisObject).getRoot();
                                        View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                        SimpleDraweeSpanTextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));
                                        //username_status.setCompoundDrawablePadding(1);
                                        username_profile_header2.setCompoundDrawables(isWebIDLE, null,null,null);
                                    } catch (Throwable e) {
                                        logger.error("Error setting mobile for userprofileheader", e);
                                    }
                                }));
                            } catch (Throwable e) {
                                logger.error("Error setting ic_web", e);
                            }*/
                        //setImageDrawableWidth((ImageView) _this, 48);
                    }
                    //-----
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopWebMobile(clientStatuses)) {
                        //imageResourceD = isDesktopWebMobile;
                        //setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);

                        View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent();
                        //View parent2 = (View) ((UserProfileHeaderView) callFrame.thisObject).getParent();
                        //Class<? extends ViewParent> find_class = ((AppCompatImageView) callFrame.thisObject).getParent().getClass();
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
                            /*try {
                                View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                                TextView username_status = parent.findViewById(Utils.getResId("username_text", "id"));
                                username_status.setCompoundDrawables(isWeb,null,isMobile,null);
                                username_status.setCompoundDrawablePadding(1);
                                patcher.patch(UserProfileHeaderView.class.getDeclaredMethod("configurePrimaryName", UserProfileHeaderViewModel.ViewState.Loaded.class), new PinePatchFn(callFrame1 -> {
                                    try {
                                        //View parent2 = (View) ((LinearLayout) callFrame1.thisObject).getParent(); //UserProfileHeaderView class or method?

                                        //View username_profile_header = UserProfileHeaderView.access$getBinding$p((UserProfileHeaderView) callFrame1.thisObject).getRoot();
                                        View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                        SimpleDraweeSpanTextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));
                                        //username_status.setCompoundDrawablePadding(1);
                                        username_profile_header2.setCompoundDrawables(isWeb, null,isMobile,null);
                                    } catch (Throwable e) {
                                        logger.error("Error setting mobile for userprofileheader", e);
                                    }
                                }));
                                //username_status.setCompoundDrawablesRelative();
                            } catch (Throwable e) {
                                logger.error("Error setting ic_web_and_mobile", e);
                            }*/
                        //setImageDrawableWidth((ImageView) _this, 64);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopWebMobileDND(clientStatuses)) {
                        //imageResourceD = isDesktopDND;
                        //imageResourceD = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_dnd", "drawable", "com.aliucord.plugins"), null);
                        //setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);

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
                            /*try {
                                View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                                TextView username_status = parent.findViewById(Utils.getResId("username_text", "id"));
                                username_status.setCompoundDrawables(isWebDND,null,isMobileDND,null);
                                username_status.setCompoundDrawablePadding(1);
                                patcher.patch(UserProfileHeaderView.class.getDeclaredMethod("configurePrimaryName", UserProfileHeaderViewModel.ViewState.Loaded.class), new PinePatchFn(callFrame1 -> {
                                    try {
                                        //View parent2 = (View) ((LinearLayout) callFrame1.thisObject).getParent(); //UserProfileHeaderView class or method?

                                        //View username_profile_header = UserProfileHeaderView.access$getBinding$p((UserProfileHeaderView) callFrame1.thisObject).getRoot();
                                        View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                        SimpleDraweeSpanTextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));
                                        //username_status.setCompoundDrawablePadding(1);
                                        username_profile_header2.setCompoundDrawables(isWebDND, null,isMobileDND,null);
                                    } catch (Throwable e) {
                                        logger.error("Error setting mobile for userprofileheader", e);
                                    }
                                }));
                                //username_status.setCompoundDrawablesRelative();
                            } catch (Throwable e) {
                                logger.error("Error setting ic_web_and_mobile_dnd", e);
                            }*/
                        //setImageDrawableWidth((ImageView) _this, 64);
                    }
                    if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopWebMobileIDLE(clientStatuses)) {
                        //imageResourceD = isDesktopWebMobileIDLE;
                        //setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);

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
                            /*try {
                                View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                                TextView username_status = parent.findViewById(Utils.getResId("username_text", "id"));
                                username_status.setCompoundDrawables(isWebIDLE,null,isMobileIDLE,null);
                                username_status.setCompoundDrawablePadding(1);
                                patcher.patch(UserProfileHeaderView.class.getDeclaredMethod("configurePrimaryName", UserProfileHeaderViewModel.ViewState.Loaded.class), new PinePatchFn(callFrame1 -> {
                                    try {
                                        //View parent2 = (View) ((LinearLayout) callFrame1.thisObject).getParent(); //UserProfileHeaderView class or method?

                                        //View username_profile_header = UserProfileHeaderView.access$getBinding$p((UserProfileHeaderView) callFrame1.thisObject).getRoot();
                                        View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                        SimpleDraweeSpanTextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));
                                        //username_status.setCompoundDrawablePadding(1);
                                        username_profile_header2.setCompoundDrawables(isWebIDLE, null,isMobileIDLE,null);
                                    } catch (Throwable e) {
                                        logger.error("Error setting mobile for userprofileheader", e);
                                    }
                                }));
                            } catch (Throwable e) {
                                logger.error("Error setting ic_web_and_mobile_idle", e);
                            }*/
                        //setImageDrawableWidth((ImageView) _this, 64);
                    }
                    //-----
                    if (imageResourceD == null) return;
                    //setImageDrawableWidth((AppCompatImageView) _this, 30);
                    //setImageDrawable((AppCompatImageView) callFrame.thisObject, (Drawable) callFrame.thisObject, 30);
                    // return callFrame.getResult();
                    //setImageDrawable((AppCompatImageView) _this, imageResourceD, 35);
                    //setImageResource((AppCompatImageView) _this, imageResource);
                    //setCompoundDrawablesRelativeWithIntrinsicBounds
                }
            }));


            /*
            patcher.patch(IconUtils.class.getDeclaredMethod("setIcon", ImageView.class, String.class, int.class, int.class, boolean.class, Function1.class, MGImages.ChangeDetector.class), new PinePatchFn(callFrame -> {
                SimpleDraweeView avatar = (SimpleDraweeView) callFrame.args[0];
                avatar.setClipToOutline(true);
                if (settings.getBool("roundedAvatars", true)) {
                    avatar.setBackground(new ShapeDrawable(new OvalShape()).setVisible(false));
                    avatar.color
                    //ShapeDrawable(OvalShape()).apply { paint.color = Color.TRANSPARENT }
                } else {
                    background =
                            GradientDrawable().apply { shape = GradientDrawable.RECTANGLE }.apply {
                        cornerRadius = DimenUtils.dpToPx(3).toFloat()
                        setColor(Color.TRANSPARENT)
                    }
                }
            }));*/

            //--------UserProfileHeaderView---------

            try {
                patcher.patch(UserProfileHeaderView.class.getDeclaredMethod("updateViewState", UserProfileHeaderViewModel.ViewState.Loaded.class), new PinePatchFn(callFrame1 -> {
                    try {
                        //setImageDrawable((AppCompatImageView) callFrame.thisObject, finalImageResourceD));
                        //View parent2 = (View) ((LinearLayout) callFrame1.thisObject).getParent(); //UserProfileHeaderView class or method?

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

            //----------RADIAL STATUS----------

            if(settings.getBool("radial_status_up", true) == true) {
                //-----------Radial Status on UserProfileHeaderView--------------

                patcher.patch(UserAvatarPresenceView.class.getDeclaredMethod("a", UserAvatarPresenceView.a.class), new PinePatchFn(callFrame -> {
                    UserAvatarPresenceView.a data = (UserAvatarPresenceView.a) callFrame.args[0];
                    if (data.b == null) return;
                    ClientStatuses clientStatuses = data.b.getClientStatuses();
                    //Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_dnd", "drawable", "com.aliucord.plugins"), null);

                    if (data.b != null) {
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isWeb(clientStatuses)) {
                            View avatar = (View) ((UserAvatarPresenceView) callFrame.thisObject).getParent();
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("avatar_cutout", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_profile_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFF3BA55C);
                            //avatar2.setBackgroundColor(Color.argb(1, 59, 165, 92));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isWebDND(clientStatuses)) {
                            View avatar = (View) ((UserAvatarPresenceView) callFrame.thisObject).getParent();
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("avatar_cutout", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_profile_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFED4245);
                            //avatar2.setBackgroundColor(Color.argb(1, 237, 66, 69));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isWebIDLE(clientStatuses)) {
                            View avatar = (View) ((UserAvatarPresenceView) callFrame.thisObject).getParent();
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("avatar_cutout", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_profile_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFFAA61A);
                            //avatar2.setBackgroundColor(Color.argb(1, 250, 166, 26));
                        }
                        //--------
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktop(clientStatuses)) {
                            View avatar = (View) ((UserAvatarPresenceView) callFrame.thisObject).getParent();
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("avatar_cutout", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_profile_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFF3BA55C);
                            //avatar2.setBackgroundColor(Color.argb(1, 59, 165, 92));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopDND(clientStatuses)) {
                            View avatar = (View) ((UserAvatarPresenceView) callFrame.thisObject).getParent();
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("avatar_cutout", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_profile_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFED4245);
                            //avatar2.setBackgroundColor(Color.argb(1, 237, 66, 69));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopIDLE(clientStatuses)) {
                            View avatar = (View) ((UserAvatarPresenceView) callFrame.thisObject).getParent();
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("avatar_cutout", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_profile_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFFAA61A);
                            //avatar2.setBackgroundColor(Color.argb(1, 250, 166, 26));
                        }
                        //--------
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isMobile(clientStatuses)) {
                            View avatar = (View) ((UserAvatarPresenceView) callFrame.thisObject).getParent();
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("avatar_cutout", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_profile_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFF3BA55C);
                            //avatar2.setBackgroundColor(Color.argb(1, 59, 165, 92));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isMobileDND(clientStatuses)) {
                            View avatar = (View) ((UserAvatarPresenceView) callFrame.thisObject).getParent();
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("avatar_cutout", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_profile_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFED4245);
                            //avatar2.setBackgroundColor(Color.argb(1, 237, 66, 69));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isMobileIDLE(clientStatuses)) {
                            View avatar = (View) ((UserAvatarPresenceView) callFrame.thisObject).getParent();
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("avatar_cutout", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_profile_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFFAA61A);
                            //avatar2.setBackgroundColor(Color.argb(1, 250, 166, 26));
                        }
                        //--------
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isWebMobile(clientStatuses)) {
                            View avatar = (View) ((UserAvatarPresenceView) callFrame.thisObject).getParent();
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("avatar_cutout", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_profile_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFF3BA55C);
                            //avatar2.setBackgroundColor(Color.argb(1, 59, 165, 92));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isWebMobileDND(clientStatuses)) {
                            View avatar = (View) ((UserAvatarPresenceView) callFrame.thisObject).getParent();
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("avatar_cutout", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_profile_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFED4245);
                            //avatar2.setBackgroundColor(Color.argb(1, 237, 66, 69));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isWebMobileIDLE(clientStatuses)) {
                            View avatar = (View) ((UserAvatarPresenceView) callFrame.thisObject).getParent();
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("avatar_cutout", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_profile_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFFAA61A);
                            //avatar2.setBackgroundColor(Color.argb(1, 250, 166, 26));
                        }
                        //--------
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndMobile(clientStatuses)) {
                            View avatar = (View) ((UserAvatarPresenceView) callFrame.thisObject).getParent();
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("avatar_cutout", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_profile_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFF3BA55C);
                            //avatar2.setBackgroundColor(Color.argb(1, 59, 165, 92));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndMobileDND(clientStatuses)) {
                            View avatar = (View) ((UserAvatarPresenceView) callFrame.thisObject).getParent();
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("avatar_cutout", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_profile_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFED4245);
                            //avatar2.setBackgroundColor(Color.argb(1, 237, 66, 69));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndMobileIDLE(clientStatuses)) {
                            View avatar = (View) ((UserAvatarPresenceView) callFrame.thisObject).getParent();
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("avatar_cutout", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_profile_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFFAA61A);
                            //avatar2.setBackgroundColor(Color.argb(1, 250, 166, 26));
                        }
                        //--------
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndWeb(clientStatuses)) {
                            View avatar = (View) ((UserAvatarPresenceView) callFrame.thisObject).getParent();
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("avatar_cutout", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_profile_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFF3BA55C);
                            //avatar2.setBackgroundColor(Color.argb(1, 59, 165, 92));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndWebDND(clientStatuses)) {
                            View avatar = (View) ((UserAvatarPresenceView) callFrame.thisObject).getParent();
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("avatar_cutout", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_profile_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFED4245);
                            //avatar2.setBackgroundColor(Color.argb(1, 237, 66, 69));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndWebIDLE(clientStatuses)) {
                            View avatar = (View) ((UserAvatarPresenceView) callFrame.thisObject).getParent();
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("avatar_cutout", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_profile_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFFAA61A);
                            //avatar2.setBackgroundColor(Color.argb(1, 250, 166, 26));
                        }
                        //--------
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopWebMobile(clientStatuses)) {
                            View avatar = (View) ((UserAvatarPresenceView) callFrame.thisObject).getParent();
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("avatar_cutout", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_profile_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFF3BA55C);
                            //avatar2.setBackgroundColor(Color.argb(1, 59, 165, 92));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopWebMobileDND(clientStatuses)) {
                            View avatar = (View) ((UserAvatarPresenceView) callFrame.thisObject).getParent();
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("avatar_cutout", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_profile_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFED4245);
                            //avatar2.setBackgroundColor(Color.argb(1, 237, 66, 69));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopWebMobileIDLE(clientStatuses)) {
                            View avatar = (View) ((UserAvatarPresenceView) callFrame.thisObject).getParent();
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("avatar_cutout", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_profile_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFFAA61A);
                            //avatar2.setBackgroundColor(Color.argb(1, 250, 166, 26));
                        }
                    }
                }));
            }

            if(settings.getBool("radial_status_cml", true) == true) {
                //-----ChannelMemberList--------
                patcher.patch(StatusView.class.getDeclaredMethod("setPresence", Presence.class), new PinePatchFn(callFrame -> {
                    Presence presence = (Presence) callFrame.args[0];
                    if (presence == null) return;
                    ClientStatuses clientStatuses = presence.getClientStatuses();
                    //Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_dnd", "drawable", "com.aliucord.plugins"), null);

                    if (presence != null) {
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isWeb(clientStatuses)) {
                            View avatar = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("channel_members_list_item_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFF3BA55C);
                            //avatar2.setBackgroundColor(Color.argb(1, 59, 165, 92));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isWebDND(clientStatuses)) {
                            View avatar = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("channel_members_list_item_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFED4245);
                            //avatar2.setBackgroundColor(Color.argb(1, 237, 66, 69));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isWebIDLE(clientStatuses)) {
                            View avatar = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("channel_members_list_item_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFFAA61A);
                            //avatar2.setBackgroundColor(Color.argb(1, 250, 166, 26));
                        }
                        //--------
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktop(clientStatuses)) {
                            View avatar = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("channel_members_list_item_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFF3BA55C);
                            //avatar2.setBackgroundColor(Color.argb(1, 59, 165, 92));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopDND(clientStatuses)) {
                            View avatar = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("channel_members_list_item_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFED4245);
                            //avatar2.setBackgroundColor(Color.argb(1, 237, 66, 69));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopIDLE(clientStatuses)) {
                            View avatar = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("channel_members_list_item_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFFAA61A);
                            //avatar2.setBackgroundColor(Color.argb(1, 250, 166, 26));
                        }
                        //--------
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isMobile(clientStatuses)) {
                            View avatar = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("channel_members_list_item_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFF3BA55C);
                            //avatar2.setBackgroundColor(Color.argb(1, 59, 165, 92));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isMobileDND(clientStatuses)) {
                            View avatar = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("channel_members_list_item_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFED4245);
                            //avatar2.setBackgroundColor(Color.argb(1, 237, 66, 69));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isMobileIDLE(clientStatuses)) {
                            View avatar = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("channel_members_list_item_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFFAA61A);
                            //avatar2.setBackgroundColor(Color.argb(1, 250, 166, 26));
                        }
                        //--------
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isWebMobile(clientStatuses)) {
                            View avatar = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("channel_members_list_item_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFF3BA55C);
                            //avatar2.setBackgroundColor(Color.argb(1, 59, 165, 92));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isWebMobileDND(clientStatuses)) {
                            View avatar = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("channel_members_list_item_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFED4245);
                            //avatar2.setBackgroundColor(Color.argb(1, 237, 66, 69));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isWebMobileIDLE(clientStatuses)) {
                            View avatar = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("channel_members_list_item_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFFAA61A);
                            //avatar2.setBackgroundColor(Color.argb(1, 250, 166, 26));
                        }
                        //--------
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndMobile(clientStatuses)) {
                            View avatar = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("channel_members_list_item_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFF3BA55C);
                            //avatar2.setBackgroundColor(Color.argb(1, 59, 165, 92));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndMobileDND(clientStatuses)) {
                            View avatar = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("channel_members_list_item_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFED4245);
                            //avatar2.setBackgroundColor(Color.argb(1, 237, 66, 69));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndMobileIDLE(clientStatuses)) {
                            View avatar = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("channel_members_list_item_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFFAA61A);
                            //avatar2.setBackgroundColor(Color.argb(1, 250, 166, 26));
                        }
                        //--------
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndWeb(clientStatuses)) {
                            View avatar = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("channel_members_list_item_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFF3BA55C);
                            //avatar2.setBackgroundColor(Color.argb(1, 59, 165, 92));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndWebDND(clientStatuses)) {
                            View avatar = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("channel_members_list_item_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFED4245);
                            //avatar2.setBackgroundColor(Color.argb(1, 237, 66, 69));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndWebIDLE(clientStatuses)) {
                            View avatar = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("channel_members_list_item_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFFAA61A);
                            //avatar2.setBackgroundColor(Color.argb(1, 250, 166, 26));
                        }
                        //--------
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopWebMobile(clientStatuses)) {
                            View avatar = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("channel_members_list_item_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFF3BA55C);
                            //avatar2.setBackgroundColor(Color.argb(1, 59, 165, 92));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopWebMobileDND(clientStatuses)) {
                            View avatar = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("channel_members_list_item_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFED4245);
                            //avatar2.setBackgroundColor(Color.argb(1, 237, 66, 69));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopWebMobileIDLE(clientStatuses)) {
                            View avatar = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                            ImageView avatar2 = avatar.findViewById(Utils.getResId("channel_members_list_item_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFFAA61A);
                            //avatar2.setBackgroundColor(Color.argb(1, 250, 166, 26));
                        }
                    }
                }));
            }

            if(settings.getBool("radial_status_dm", true) == true) {
                //--------DM List---------

                patcher.patch(WidgetChannelsListAdapter.ItemChannelPrivate.class.getDeclaredMethod("onConfigure", int.class, ChannelListItem.class), new PinePatchFn(callFrame -> {
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
                    if (presence == null) return;
                    ClientStatuses clientStatuses = presence.getClientStatuses();
                    //Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_dnd", "drawable", "com.aliucord.plugins"), null);

                    if(presence != null) {
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isWeb(clientStatuses)) {
                            ImageView avatar2 = layout.findViewById(Utils.getResId("channels_list_item_private_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFF3BA55C);
                            //avatar2.setBackgroundColor(Color.argb(1, 59, 165, 92));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isWebDND(clientStatuses)) {
                            ImageView avatar2 = layout.findViewById(Utils.getResId("channels_list_item_private_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFED4245);
                            //avatar2.setBackgroundColor(Color.argb(1, 237, 66, 69));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isWebIDLE(clientStatuses)) {
                            ImageView avatar2 = layout.findViewById(Utils.getResId("channels_list_item_private_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFFAA61A);
                            //avatar2.setBackgroundColor(Color.argb(1, 250, 166, 26));
                        }
                        //--------
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktop(clientStatuses)) {
                            ImageView avatar2 = layout.findViewById(Utils.getResId("channels_list_item_private_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFF3BA55C);
                            //avatar2.setBackgroundColor(Color.argb(1, 59, 165, 92));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopDND(clientStatuses)) {
                            ImageView avatar2 = layout.findViewById(Utils.getResId("channels_list_item_private_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFED4245);
                            //avatar2.setBackgroundColor(Color.argb(1, 237, 66, 69));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopIDLE(clientStatuses)) {
                            ImageView avatar2 = layout.findViewById(Utils.getResId("channels_list_item_private_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFFAA61A);
                            //avatar2.setBackgroundColor(Color.argb(1, 250, 166, 26));
                        }
                        //--------
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isMobile(clientStatuses)) {
                            ImageView avatar2 = layout.findViewById(Utils.getResId("channels_list_item_private_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFF3BA55C);
                            //avatar2.setBackgroundColor(Color.argb(1, 59, 165, 92));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isMobileDND(clientStatuses)) {
                            ImageView avatar2 = layout.findViewById(Utils.getResId("channels_list_item_private_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFED4245);
                            //avatar2.setBackgroundColor(Color.argb(1, 237, 66, 69));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isMobileIDLE(clientStatuses)) {
                            ImageView avatar2 = layout.findViewById(Utils.getResId("channels_list_item_private_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFFAA61A);
                            //avatar2.setBackgroundColor(Color.argb(1, 250, 166, 26));
                        }
                        //--------
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isWebMobile(clientStatuses)) {
                            ImageView avatar2 = layout.findViewById(Utils.getResId("channels_list_item_private_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFF3BA55C);
                            //avatar2.setBackgroundColor(Color.argb(1, 59, 165, 92));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isWebMobileDND(clientStatuses)) {
                            ImageView avatar2 = layout.findViewById(Utils.getResId("channels_list_item_private_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFED4245);
                            //avatar2.setBackgroundColor(Color.argb(1, 237, 66, 69));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isWebMobileIDLE(clientStatuses)) {
                            ImageView avatar2 = layout.findViewById(Utils.getResId("channels_list_item_private_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFFAA61A);
                            //avatar2.setBackgroundColor(Color.argb(1, 250, 166, 26));
                        }
                        //--------
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndMobile(clientStatuses)) {
                            ImageView avatar2 = layout.findViewById(Utils.getResId("channels_list_item_private_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFF3BA55C);
                            //avatar2.setBackgroundColor(Color.argb(1, 59, 165, 92));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndMobileDND(clientStatuses)) {
                            ImageView avatar2 = layout.findViewById(Utils.getResId("channels_list_item_private_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFED4245);
                            //avatar2.setBackgroundColor(Color.argb(1, 237, 66, 69));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndMobileIDLE(clientStatuses)) {
                            ImageView avatar2 = layout.findViewById(Utils.getResId("channels_list_item_private_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFFAA61A);
                            //avatar2.setBackgroundColor(Color.argb(1, 250, 166, 26));
                        }
                        //--------
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndWeb(clientStatuses)) {
                            ImageView avatar2 = layout.findViewById(Utils.getResId("channels_list_item_private_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFF3BA55C);
                            //avatar2.setBackgroundColor(Color.argb(1, 59, 165, 92));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndWebDND(clientStatuses)) {
                            ImageView avatar2 = layout.findViewById(Utils.getResId("channels_list_item_private_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFED4245);
                            //avatar2.setBackgroundColor(Color.argb(1, 237, 66, 69));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopAndWebIDLE(clientStatuses)) {
                            ImageView avatar2 = layout.findViewById(Utils.getResId("channels_list_item_private_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFFAA61A);
                            //avatar2.setBackgroundColor(Color.argb(1, 250, 166, 26));
                        }
                        //--------
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopWebMobile(clientStatuses)) {
                            ImageView avatar2 = layout.findViewById(Utils.getResId("channels_list_item_private_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFF3BA55C);
                            //avatar2.setBackgroundColor(Color.argb(1, 59, 165, 92));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopWebMobileDND(clientStatuses)) {
                            ImageView avatar2 = layout.findViewById(Utils.getResId("channels_list_item_private_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFED4245);
                            //avatar2.setBackgroundColor(Color.argb(1, 237, 66, 69));
                        }
                        if (clientStatuses != null && PresenceUtils.INSTANCE.isDesktopWebMobileIDLE(clientStatuses)) {
                            ImageView avatar2 = layout.findViewById(Utils.getResId("channels_list_item_private_avatar", "id"));
                            Drawable radial_status = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_radial_status", "drawable", "com.aliucord.plugins"), null);

                            avatar2.setPadding(8, 8, 8, 8);
                            avatar2.setBackground(radial_status);
                            Objects.requireNonNull(radial_status).setTint(0xFFFAA61A);
                            //avatar2.setBackgroundColor(Color.argb(1, 250, 166, 26));
                        }
                    }
                }));
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

                        /*View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent();
                        SimpleDraweeSpanTextView username_status = parent.findViewById(Utils.getResId("username_text", "id"));

                        DraweeSpanStringBuilder first_icon = new DraweeSpanStringBuilder();
                        first_icon.append(" ", new ImageSpan(isDesktopDND,1), 0);

                        DraweeSpanStringBuilder second_icon = new DraweeSpanStringBuilder();
                        second_icon.append(" ", new ImageSpan(isWebDND,1), 0);

                        DraweeSpanStringBuilder third_icon = new DraweeSpanStringBuilder();
                        third_icon.append("", new ImageSpan(isMobileDND, 1),0);

                        username_status.append(" ");
                        username_status.append(first_icon);
                        username_status.append(" ");
                        username_status.append(second_icon);
                        username_status.append(" ");
                        username_status.append(third_icon);*/
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
                //PluginManager.stopPlugin("BetterStatus");
                //PluginManager.startPlugin("BetterStatus");
                Utils.showToast("Please restart Aliucord to apply");
            });
            addView(radial_status_dm);

            CheckedSetting radial_status_up = Utils.createCheckedSetting(requireContext(), CheckedSetting.ViewType.SWITCH, "Radial Status (UserProfile)", "Shows a status ring around the user avatar in the UserProfile.");
            radial_status_up.setChecked(false);
            radial_status_up.setChecked(settings.getBool("radial_status_up", false));
            radial_status_up.setOnCheckedListener(checked -> {
                settings.setBool("radial_status_up", checked);
                //PluginManager.stopPlugin("BetterStatus");
                //PluginManager.startPlugin("BetterStatus");
                Utils.showToast("Please restart Aliucord to apply");
            });
            addView(radial_status_up);

        }
    }
}
