package com.aliucord.plugins;

import android.annotation.SuppressLint;
import android.content.Context;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import com.aliucord.Logger;
import com.aliucord.Utils;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.discord.api.presence.ClientStatuses;
import com.aliucord.plugins.betterstatus.PresenceUtils;
import com.aliucord.patcher.PinePatchFn;
import com.discord.api.user.User;
import com.discord.app.App;
import com.discord.databinding.UserProfileHeaderViewBinding;
import com.discord.databinding.WidgetChannelMembersListItemUserBinding;
import com.discord.models.presence.Presence;
import com.discord.views.StatusView;
import com.discord.views.UsernameView;
import com.discord.widgets.channels.memberlist.adapter.ChannelMembersListAdapter;
import com.discord.widgets.channels.memberlist.adapter.ChannelMembersListViewHolderMember;
import com.discord.widgets.user.profile.UserProfileHeaderView;
import com.discord.widgets.user.profile.UserProfileHeaderViewModel;
import com.facebook.drawee.span.DraweeSpanStringBuilder;
import com.facebook.drawee.span.SimpleDraweeSpanTextView;

import androidx.appcompat.widget.AppCompatImageView;

import java.util.*;

import kotlin.jvm.functions.Function0;

@SuppressWarnings("unused")
@AliucordPlugin
public class BetterStatus extends Plugin {

    public BetterStatus() {
        needsResources = true;
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
        //AppCompatImageView status = appCompatImageView.findViewById(0x7F0A01C9);
        //appCompatImageView.getLayoutParams().width = width;
    }

    public void setImageDrawable2(AppCompatImageView appCompatImageView, Drawable imageResource, int width)
    {
        appCompatImageView.setImageDrawable(imageResource);
        appCompatImageView.setAdjustViewBounds(true);
        appCompatImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        //AppCompatImageView status = appCompatImageView.findViewById(0x7F0A01C9);
        appCompatImageView.getLayoutParams().width = width;
    }

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
        //var bindingField = ChannelMembersListViewHolderMember.class.getDeclaredField("binding");
        //bindingField.setAccessible(true);

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

        /*
        patcher.patch(ChannelMembersListViewHolderMember.class, "bind", new Class<?>[]{ ChannelMembersListAdapter.Item.Member.class, Function0.class}, new PinePatchFn(callFrame -> {
            try {
                WidgetChannelMembersListItemUserBinding binding = (WidgetChannelMembersListItemUserBinding) bindingField.get(callFrame.thisObject);
                ConstraintLayout layout = (ConstraintLayout) binding.getRoot();
                ChannelMembersListAdapter.Item.Member user = (ChannelMembersListAdapter.Item.Member) callFrame.args[0];
                String tag = settings.getString(String.valueOf(user.getUserId()), null);
                //boolean verified = settings.getBool(user.getUserId() + "_verified", false);

                if(user.isBot() == false) {
                    TextView tagText = (TextView) layout.findViewById(Utils.getResId("username_tag", "id"));
                    tagText.setText(String.valueOf(tag));
                    //if(user.getUserId() == 298295889720770563L || verified == true) {
                        tagText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_mobile, 0, 0, 0);
                    //}
                    tagText.setVisibility(View.VISIBLE);
                }
            } catch(Throwable e) {
                e.printStackTrace();
            }
        }));
        */


        patcher.patch(StatusView.class.getDeclaredMethod("setPresence", Presence.class), new PinePatchFn(callFrame -> {
            Drawable imageResourceD=null;

            int imageResource = 0;
            Presence presence = (Presence) callFrame.args[0];
            if(presence == null) return;
            ClientStatuses clientStatuses = presence.getClientStatuses();
            //int id = AppCompatImageView.generateViewId();

            if(presence != null) {
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
                    imageResourceD = isWebMobile;
                    setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);
                    /*try {
                        //View parent = (View) ((AppCompatImageView) callFrame.thisObject).getParent(); //UsernameView class or method?
                        //TextView username_status = parent.findViewById(Utils.getResId("username_text", "id"));
                        //username_status.setCompoundDrawables(isMobile, null, null, null);
                        Drawable finalImageResourceD = imageResourceD;
                        patcher.patch(UserProfileHeaderView.class.getDeclaredMethod("configurePrimaryName", UserProfileHeaderViewModel.ViewState.Loaded.class), new PinePatchFn(callFrame1 -> {
                            try {
                                setImageDrawable2((AppCompatImageView) callFrame.thisObject, finalImageResourceD, isWebMobile.getIntrinsicWidth());
                                //View parent2 = (View) ((LinearLayout) callFrame1.thisObject).getParent(); //UserProfileHeaderView class or method?

                                //View username_profile_header = UserProfileHeaderView.access$getBinding$p((UserProfileHeaderView) callFrame1.thisObject).getRoot();
                                //View username_profile_header = (View) ((UserProfileHeaderView) callFrame1.thisObject).getParent();
                                //SimpleDraweeSpanTextView username_profile_header2 = username_profile_header.findViewById(Utils.getResId("username_text", "id"));
                                //username_status.setCompoundDrawablePadding(1);
                                //username_profile_header2.setCompoundDrawables(isMobile, null,null,null);
                            } catch (Throwable e) {
                                logger.error("Error setting mobile for userprofileheader", e);
                            }
                        }));
                        //username_status.setCompoundDrawablePadding(1);
                    } catch (Throwable e) {
                        logger.error("Error setting ic_mobile", e);
                    }*/
                    //TextView username = ((TextView) callFrame.thisObject).findViewById(0x7f0a0209);
                    //username.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_mobile, 0, 0, 0);
                }
                if (clientStatuses != null && PresenceUtils.INSTANCE.isWebMobileDND(clientStatuses)) {
                    imageResourceD = isWebMobileDND;
                    setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);

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
                    imageResourceD = isWebMobileIDLE;
                    setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);

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
                    imageResourceD = isDesktopMobile;
                    setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);

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
                    imageResourceD = isDesktopMobileDND;
                    setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);

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
                    imageResourceD = isDesktopMobileIDLE;
                    setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);

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
                    imageResourceD = isDesktopWeb;
                    setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);

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
                    imageResourceD = isDesktopWebDND;
                    setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);

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
                    imageResourceD = isDesktopWebIDLE;
                    setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);

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
                    imageResourceD = isDesktopWebMobile;
                    setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);

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
                    imageResourceD = isDesktopWebMobileDND;
                    setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);

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
                    imageResourceD = isDesktopWebMobileIDLE;
                    setImageDrawable((AppCompatImageView) callFrame.thisObject, imageResourceD);

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
                if(imageResourceD == null) return;
                //setImageDrawableWidth((AppCompatImageView) _this, 30);
                //setImageDrawable((AppCompatImageView) callFrame.thisObject, (Drawable) callFrame.thisObject, 30);
                // return callFrame.getResult();
                //setImageDrawable((AppCompatImageView) _this, imageResourceD, 35);
                //setImageResource((AppCompatImageView) _this, imageResource);
                //setCompoundDrawablesRelativeWithIntrinsicBounds
            }
        }));}

    @Override
    public void stop(Context context) {
        patcher.unpatchAll();
    }
}
