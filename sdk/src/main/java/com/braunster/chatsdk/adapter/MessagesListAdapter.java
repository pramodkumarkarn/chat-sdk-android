package com.braunster.chatsdk.adapter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.braunster.chatsdk.R;
import com.braunster.chatsdk.Utils.Debug;
import com.braunster.chatsdk.Utils.DialogUtils;
import com.braunster.chatsdk.Utils.ImageUtils;
import com.braunster.chatsdk.Utils.helper.ChatSDKUiHelper;
import com.braunster.chatsdk.Utils.volley.ChatSDKToast;
import com.braunster.chatsdk.Utils.volley.VolleyUtils;
import com.braunster.chatsdk.dao.BMessage;
import com.braunster.chatsdk.dao.BUser;
import com.braunster.chatsdk.dao.entities.BMessageEntity;
import com.braunster.chatsdk.network.BDefines;
import com.braunster.chatsdk.view.ChatBubbleImageView2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by itzik on 6/5/2014.
 */
public class MessagesListAdapter extends BaseAdapter{

    // FIXME  fix content overlap the hour.
    private static final String TAG = MessagesListAdapter.class.getSimpleName();
    private static final boolean DEBUG = Debug.MessagesListAdapter;

    /* Row types */
    private static final int TYPE_TEXT_USER = 0;
    private static final int TYPE_TEXT_FRIEND = 1;
    private static final int TYPE_IMAGE_USER = 2;
    private static final int TYPE_IMAGE_FRIEND = 3;
    private static final int TYPE_LOCATION_USER = 4;
    private static final int TYPE_LOCATION_FRIEND = 5;

    private int textUserRowResId = R.layout.chat_sdk_row_text_message_user, textFriendRowResId = R.layout.chat_sdk_row_text_message_friend,
            imageUserRowResId = R.layout.chat_sdk_row_image_message_user, imageFriendRowResId = R.layout.chat_sdk_row_image_message_friend,
            locationUserResId = R.layout.chat_sdk_row_image_message_user, locationFriendRowResId = R.layout.chat_sdk_row_image_message_friend;

    int maxWidth;

    private boolean hideMultipleImages = false;

    private Activity mActivity;

    private List<MessageListItem> listData = new ArrayList<MessageListItem>();

    private SimpleDateFormat customDateFormat = null;
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
    private List<String> cacheKeys = new ArrayList<String>();

    private long userID = 0;

    private boolean isScrolling = false;

    private LayoutInflater inflater;

    private int type = -1;

    private int textColor = -1991;

    public MessagesListAdapter(Activity activity, Long userID){
        mActivity = activity;
        this.userID = userID;
        inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        maxWidth = (int) (activity.getResources().getDisplayMetrics().density * 200);
    }

    public MessagesListAdapter(Activity activity, Long userID, List<MessageListItem> listData){
        mActivity = activity;

        this.userID = userID;

        if (listData == null)
            listData = new ArrayList<MessageListItem>();

        this.listData = (List<MessageListItem>) listData;
        inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        maxWidth = (int) (activity.getResources().getDisplayMetrics().density * 200);
    }

    @Override
    public int getViewTypeCount() {
        return 6;
    }

    @Override
    public int getItemViewType(int position) {
        return listData.get(position).rowType;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int i) {
        return listData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return listData.get(i).id;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View row;
        row = view;
        ViewHolder holder;
        type = getItemViewType(position);
        final MessageListItem message = listData.get(position);
        final boolean sender = message.rowType % 2 == 0;

        if (row == null)
        {
            holder = new ViewHolder();
            switch (type)
            {
                case TYPE_TEXT_USER:
                    row = inflater.inflate(textUserRowResId, null);

                    holder.txtContent = (TextView) row.findViewById(R.id.txt_content);

                    break;

                case TYPE_TEXT_FRIEND:

                    row = inflater.inflate(textFriendRowResId, null);

                    holder.txtContent = (TextView) row.findViewById(R.id.txt_content);

                    break;

                case TYPE_IMAGE_USER:
                    row = inflater.inflate(imageUserRowResId, null);

                    holder.progressBar = (ProgressBar) row.findViewById(R.id.chat_sdk_progress_bar);
                    holder.image = (ChatBubbleImageView2) row.findViewById(R.id.chat_sdk_image);

                    break;

                case TYPE_IMAGE_FRIEND:
                    row = inflater.inflate(imageFriendRowResId, null);

                    holder.progressBar = (ProgressBar) row.findViewById(R.id.chat_sdk_progress_bar);
                    holder.image = (ChatBubbleImageView2) row.findViewById(R.id.chat_sdk_image);
                    break;

                case TYPE_LOCATION_USER:
                    row = inflater.inflate(locationUserResId, null);

                    holder.progressBar = (ProgressBar) row.findViewById(R.id.chat_sdk_progress_bar);
                    holder.image = (ChatBubbleImageView2) row.findViewById(R.id.chat_sdk_image);

                    break;

                case TYPE_LOCATION_FRIEND:
                    row = inflater.inflate(locationFriendRowResId, null);

                    holder.progressBar = (ProgressBar) row.findViewById(R.id.chat_sdk_progress_bar);
                    holder.image = (ChatBubbleImageView2) row.findViewById(R.id.chat_sdk_image);

                    break;


            }

            loadDefaults(holder, row, sender);

            row.setTag(holder);
        }
        else
            holder = (ViewHolder) row.getTag();

        if (DEBUG) Log.d(TAG, "Message, Type: " + message.type  + " Row Type: " + type + ", Text: " + message.text);

        switch (type)
        {
            case TYPE_TEXT_USER:
            case TYPE_TEXT_FRIEND:

                holder.txtContent.setText(message.text == null ? "ERROR" : message.text);

                // Show links in text view if has any.
                holder.txtContent.setMovementMethod(LinkMovementMethod.getInstance());
                Linkify.addLinks(holder.txtContent, Linkify.ALL);

                if (textColor != -1991)
                    holder.txtContent.setTextColor(textColor);

                animateContent((View) holder.txtContent.getParent(), null);

                break;

            case TYPE_IMAGE_USER:
            case TYPE_IMAGE_FRIEND:

                getBubbleImageViewFromRow(holder.image, holder.progressBar, message);

                // Show the image in a dialog on click.
                holder.image.setOnClickListener(new showImageDialogClickListener());
                break;

            case TYPE_LOCATION_USER:
            case TYPE_LOCATION_FRIEND:

                getBubbleImageViewFromRow(holder.image, holder.progressBar,  message);

                // Open google maps on click.
                holder.image.setOnClickListener(new openGoogleMaps());

                break;
        }

        // Load profile picture.
        if (!hideMultipleImages || position == 0 || message.sender != listData.get(position-1).sender) {
            loadProfilePic(holder.profilePicImage, message.profilePicUrl, sender);
        } else holder.profilePicImage.setVisibility(View.INVISIBLE);

        // Set the time of the sending.
        holder.txtTime.setText(message.time);
        animateSides(holder.txtTime, sender, null);

        return row;
    }

    /** @return true if the item is added to the list.*/
    public boolean addRow(MessageListItem newItem){
        if (DEBUG) Log.d(TAG, "AddRow");

        // Bad data.
        if (newItem == null)
            return false;

        // Dont add message that does not have entity id and the status of the message is not sending.
        if (newItem.entityId == null && newItem.status != BMessage.Status.SENDING)
        {
            if (DEBUG) Log.d(TAG, "Entity id is null, Message text: "  + newItem.text);
            return false;
        }

        // Check for duplicates, And update the message status if its already exist.
        for (MessageListItem item : listData)
        {
            if (item.entityId != null && item.entityId.equals(newItem.entityId) || item.id == newItem.id)
            {
                item.status = newItem.status;
                notifyDataSetChanged();
                return false;
            }
        }

        listData.add(newItem);

        notifyDataSetChanged();

        return true;
    }

    public boolean addRow(BMessage message){
        return addRow(MessageListItem.fromBMessage(message, userID, maxWidth, customDateFormat));
    }

    public void setListData(List<MessageListItem> listData) {
        this.listData = listData;
        notifyDataSetChanged();
    }

    public void clear(){
        listData.clear();
        notifyDataSetChanged();
    }

    public void hideMultipleImages(boolean hide){
        this.hideMultipleImages = hide;
    }

    public List<String> getCacheKeys() {
        return cacheKeys;
    }

    public List<MessageListItem> getListData() {
        return listData;
    }

    private void loadDefaults(ViewHolder holder, View row, boolean sender){
        // Load profile picture.
        holder.profilePicImage = (CircleImageView) row.findViewById(R.id.img_user_image);
        holder.txtTime = (TextView) row.findViewById(R.id.txt_time);
    }

    /** Load profile picture for given url and image view.*/
    private void loadProfilePic(final CircleImageView circleImageView, final String url, final boolean sender){

        if (url == null)
        {
            circleImageView.setImageResource(R.drawable.ic_profile);
            return;
        }

        circleImageView.setTag(url);

        VolleyUtils.getImageLoader().get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(final ImageLoader.ImageContainer response, boolean isImmediate) {

                // Checking to see that there is no new rewuest on this image.
                if (circleImageView.getTag() != null && !circleImageView.getTag().equals(url))
                    return;

                if (isImmediate && response.getBitmap() == null)
                {
                    circleImageView.setImageResource(R.drawable.ic_profile);
                    return;
                }

                if (response.getBitmap() != null)
                {
                    if (!isScrolling)
                    {
                        circleImageView.setImageBitmap(response.getBitmap());
                    }
                    else
                    {
                        animateSides(circleImageView, !sender, new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                circleImageView.setImageBitmap(response.getBitmap());
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                        circleImageView.getAnimation().start();
                    }
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                circleImageView.setImageResource(R.drawable.ic_profile);
            }
        }, circleImageView.getWidth(), circleImageView.getWidth());
    }

    /** Get a ready image view for row position. The picture will be loaded to the bubble image view in the background using Volley. */
    private ChatBubbleImageView2 getBubbleImageViewFromRow(final ChatBubbleImageView2 image, final ProgressBar progressBar, final MessageListItem message){
        // Save the message text to the image tag so it could be found on the onClick.
        image.setTag(message.text);

        // Coloring the message
        int bubbleColor = message.color;

        // Loading the url.
        final ChatBubbleImageView2.LoadDone loadDone = new ChatBubbleImageView2.LoadDone() {

            @Override
            public void onDone() {
                if (progressBar.getVisibility() == View.VISIBLE) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void immediate(boolean immediate) {
                if (immediate){
                    if (progressBar.getVisibility() == View.VISIBLE) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    animateContent(image, null);
                }
                else
                {
                    image.clearCanvas();
                    if (progressBar.getVisibility() == View.INVISIBLE) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }
            }
        };

        if (message.dimensions != null)
        {
            // Getting the dimensions of the image so we can calc it final size and prepare room for it in the list view.
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) image.getLayoutParams();
            params.width = (int) (message.dimensions[0] + image.getImagePadding() + image.getPointSize());
            params.height = message.dimensions[1] + image.getImagePadding();
            image.setLayoutParams(params);

            // Saving the url so we could remove it later on.
            cacheKeys.add(VolleyUtils.BitmapCache.getCacheKey(message.url + ChatBubbleImageView2.URL_FIX, 0, 0));
            image.loadFromUrl(message.url, loadDone, message.dimensions[0], message.dimensions[1]);
        }

        return image;
    }

    /** Click listener for an image view, A dialog that show the image will show for each click.*/
    public class showImageDialogClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            ChatSDKUiHelper.hideSoftKeyboard(mActivity);

            if (DEBUG) Log.v(TAG, "OnClick - Location");
            // Show the location image.
            if (v.getTag() != null)
            {
                String url;
                String [] urls = ((String) v.getTag()).split(BDefines.DIVIDER);
                url = urls[0];

                // Saving the url so we could remove it later on.
                cacheKeys.add(VolleyUtils.BitmapCache.getCacheKey(url, 0, 0));

                DialogUtils.getImageDialog(mActivity, url, DialogUtils.LoadTypes.LOAD_FROM_URL).
                        showAtLocation(v, Gravity.CENTER, 0, 0);
            }
            else
            {
                ChatSDKToast.toastAlert(mActivity, "Cant show image.");
            }


        }
    }

    /** Click listener for the view location button for location messages. The click will open Google Maps for the location.*/
    public class openGoogleMaps implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            String[] loc = ((String)v.getTag()).split(BDefines.DIVIDER);

            openLocationInGoogleMaps(Double.parseDouble(loc[0]), Double.parseDouble(loc[1]));
        }
        private void openLocationInGoogleMaps(Double latitude, Double longitude){
            try {
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f (%s)", latitude, longitude, latitude, longitude, "Mark");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                mActivity.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                ChatSDKToast.toastAlert(mActivity, "This phone does not have google maps.");
            }
        }
    }

    public List<MessageListItem> makeList(List<BMessage> list){
        return MessageListItem.makeList(mActivity, userID, list, customDateFormat);
    }

    public static class MessageListItem{
        private String entityId, profilePicUrl, time, text, textColor;
        private int type, status, color, rowType;
        private long sender;
        private long id;
        private int[] dimensions = null;
        private String url;

        private MessageListItem(long id, String entityId, int type, int rowType, int status,
                                long senderID, String profilePicUrl, String time,
                                String text, String color, String textColor) {
            this.type = type;
            this.id = id;
            this.status = status;
            this.sender = senderID;
            this.entityId = entityId;
            this.profilePicUrl = profilePicUrl;
            this.time = time;
            this.text = text;
            this.color = setColor(color);
            this.textColor = textColor;
            this.rowType = rowType;

            if (type == BMessage.Type.IMAGE || type == BMessage.Type.LOCATION)
                url = getUrl(text, type);
        }


        public static MessageListItem fromBMessage(BMessage message, Long userID, int maxWidth, SimpleDateFormat simpleDateFormat){

            // If null that means no custom format was added to the adapter so we use the default.
            if (simpleDateFormat == null)
                simpleDateFormat = getFormat(message);

            BUser user = message.getBUserSender();

            MessageListItem msg = new MessageListItem( message.getId(),
                    message.getEntityID(),
                    message.getType(),
                    getRowType(message.getType(), user.getId(), userID),
                    message.getStatusOrNull(),
                    user.getId(),
                    user.getThumbnailPictureURL(),
                    String.valueOf(simpleDateFormat.format(message.getDate())),
                    message.getText(),
                    user.getMessageColor(),
                    user.getTextColor());

            msg.setDimension(maxWidth);

            return msg;
        }

        public static List<MessageListItem> makeList(Activity activity, Long userID, List<BMessage> messages, SimpleDateFormat simpleDateFormat){
            List<MessageListItem> list = new ArrayList<MessageListItem>();

            int maxWidth = (int) (activity.getResources().getDisplayMetrics().density * 200);

            MessageListItem i;
            for (BMessage message : messages)
                if (message.getEntityID() != null)
                {
                    i = fromBMessage(message, userID, maxWidth, simpleDateFormat);

                    /*Fixme Due to old data*/
                    if (i.type != BMessage.Type.TEXT && i.dimensions == null)
                        continue;

                    list.add(i);
                }

            // We need to reverse the list so the newest data would be on the top again.
            Collections.reverse(list);

            return list;
        }

        private static SimpleDateFormat getFormat(BMessage message){

            Date curTime = new Date();
            long interval = (curTime.getTime() - message.getDate().getTime()) / 1000L;

            // More then a day ago
            if (interval > 3600 * 24)
            {
                // More then a year
                if (interval > 3600 * 24 * 365)
                {
                    simpleDateFormat.applyPattern("MM/yy");
                    return simpleDateFormat;
                }
                else {
                    simpleDateFormat.applyPattern("MMM dd");
                    return simpleDateFormat;
                }
            }
            else
            {
                simpleDateFormat.applyPattern("HH:mm");
                return simpleDateFormat;
            }
        }

        private static int getColorFromDec(String color){
            String[] split = color.split(" ");

            if (split.length != 4)
                return BMessage.randomColor();

            int bubbleColor = -1;

            bubbleColor = Color.argb(Integer.parseInt(split[3]), (int) (255 * Float.parseFloat(split[0])), (int) (255 * Float.parseFloat(split[1])), (int) (255 * Float.parseFloat(split[2])));

            return bubbleColor;
        }

        private static int setColor(String color){
            // Coloring the message
            int bubbleColor = -1;
            if (color != null && !color.equals("Red"))
            {
                try{
                    bubbleColor = Color.parseColor(color);
                }
                catch (Exception e){}

                if (bubbleColor == -1)
                {
                    bubbleColor = getColorFromDec(color);
                    if (DEBUG) Log.d(TAG, "Color: " + bubbleColor);
                }
            }
            else bubbleColor = BMessage.randomColor();

            return bubbleColor;
        }

        private static int getRowType(int messageType, long senderId, long curUserID){
            // Setting the row type.
            int type;
            switch (messageType)
            {
                case BMessage.Type.TEXT:
                    if (senderId == curUserID)
                        type = TYPE_TEXT_USER;
                    else type = TYPE_TEXT_FRIEND;
                    break;

                case BMessage.Type.LOCATION:
                    if (senderId == curUserID)
                        type = TYPE_LOCATION_USER;
                    else type = TYPE_LOCATION_FRIEND;
                    break;

                case BMessage.Type.IMAGE:
                    if (senderId == curUserID)
                        type = TYPE_IMAGE_USER;
                    else type = TYPE_IMAGE_FRIEND;
                    break;

                default:
                    type = -1;
            }

            if (DEBUG) Log.d(TAG, "getRowType, RowType: " + type + ", Msg type: " + messageType + ", SenderID: " + senderId + ", userID: " + curUserID);

            return type;
        }

        private static String getUrl(String text, int type){
            /*FIXME because of old data we need to do some testing adn extra checking*/
            String url = "";
            String [] urls = text.split(BDefines.DIVIDER);
            if (type == BMessageEntity.Type.IMAGE)
            {
                if (urls.length > 1)
                {
                    url = urls[1];
                }
                else url = urls[0];
            }
            else if (type == BMessageEntity.Type.LOCATION)
            {
                if (urls.length == 1)
                    urls = text.split("&");

                try {
                    if (urls.length > 3)
                        url = urls[3];
                    else url = urls[2];
                } catch (Exception e) {
//                e.printStackTrace();
                }
            }

            return url;
        }

        private void setDimension(int maxWidth){
            try {
                String[] data = text.split(BDefines.DIVIDER);
                dimensions = ImageUtils.getDimentionsFromString(data[data.length - 1]);
                dimensions = ImageUtils.calcNewImageSize(dimensions, maxWidth);

                if (dimensions.length != 2)
                    dimensions = null;

                if (DEBUG) Log.d(TAG, "dim: " + dimensions[0] + ", " + dimensions[1]);
            }catch (Exception e){  dimensions = null;}
        }

        private int getColor(){
            if (status == BMessageEntity.Status.SENDING)
                return Color.parseColor(BDefines.Defaults.MessageSendingColor);

            return color;
        }
    }

    class ViewHolder{
        CircleImageView profilePicImage;
        TextView txtTime;
        ChatBubbleImageView2 image;
        TextView txtContent;
        ProgressBar progressBar;
    }

    public void setScrolling(boolean isScrolling) {
        this.isScrolling = isScrolling;
    }

    private void animateSides(View view, boolean fromLeft, Animation.AnimationListener animationListener){
        if (!isScrolling)
            return;

        if (fromLeft)
            view.setAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.expand_slide_form_left));
        else view.setAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.expand_slide_form_right));

        view.getAnimation().setAnimationListener(animationListener);
        view.animate();
    }

    private void animateContent(View view, Animation.AnimationListener animationListener){
        if (!isScrolling)
            return;

        view.setAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.fade_in_expand));
        view.getAnimation().setAnimationListener(animationListener);
        view.animate();
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }


    /*Set row types resource id*/

    public void setTextUserRowResId(int textUserRowResId) {
        this.textUserRowResId = textUserRowResId;
    }

    public void setTextFriendRowResId(int textFriendRowResId) {
        this.textFriendRowResId = textFriendRowResId;
    }

    public void setImageUserRowResId(int imageUserRowResId) {
        this.imageUserRowResId = imageUserRowResId;
    }

    public void setImageFriendRowResId(int imageFriendRowResId) {
        this.imageFriendRowResId = imageFriendRowResId;
    }

    public void setLocationUserResId(int locationUserResId) {
        this.locationUserResId = locationUserResId;
    }

    public void setLocationFriendRowResId(int locationFriendRowResId) {
        this.locationFriendRowResId = locationFriendRowResId;
    }

    public void setCustomDateFormat(SimpleDateFormat customDateFormat) {
        this.customDateFormat = customDateFormat;
    }
}