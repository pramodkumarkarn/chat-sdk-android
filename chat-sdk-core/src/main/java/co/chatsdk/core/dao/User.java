package co.chatsdk.core.dao;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.chatsdk.core.base.AbstractEntity;
import co.chatsdk.core.defines.Availability;
import co.chatsdk.core.events.NetworkEvent;
import co.chatsdk.core.image.ImageUtils;
import co.chatsdk.core.interfaces.UserListItem;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.types.ConnectionType;
import co.chatsdk.core.utils.StringChecker;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;

import static co.chatsdk.core.image.ImageBuilder.bitmapForURL;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS
// KEEP INCLUDES - put your token includes here

@Entity
public class User extends AbstractEntity implements UserListItem {

    @Id
    private Long id;
    private String entityID;
    private Integer authenticationType;
    private Date lastOnline;
    private Boolean isOnline;

    @ToMany(referencedJoinProperty = "userId")
    private List<UserMetaValue> metaValues;
    
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1507654846)
    private transient UserDao myDao;


    @Generated(hash = 1863692680)
    public User(Long id, String entityID, Integer authenticationType, Date lastOnline, Boolean isOnline) {
        this.id = id;
        this.entityID = entityID;
        this.authenticationType = authenticationType;
        this.lastOnline = lastOnline;
        this.isOnline = isOnline;
    }

    @Generated(hash = 586692638)
    public User() {
    }

    public List<User> getContacts() {
        return getContacts(ConnectionType.Contact);
    }

    public List<User> getContacts(ConnectionType type) {
        List<User> contactList = new ArrayList<>();

        // For some reason the default ContactLinks do not persist, have to find in DB
        List<ContactLink> contactLinks = DaoCore.fetchEntitiesWithProperty(ContactLink.class,
                ContactLinkDao.Properties.LinkOwnerUserDaoId, this.getId());

        for (ContactLink contactLink : contactLinks){
            if(contactLink.getConnectionType().equals(type)) {
                User user = contactLink.getUser();
                if(user != null) {
                    contactList.add(contactLink.getUser());
                }
            }
        }

        return contactList;
    }

    public void addContact(User user, ConnectionType type) {

        if (user.isMe()) {
            return;
        }

        // Retrieve contacts
        List contacts = getContacts(type);

        // Check if user is already in contact list
        if (contacts.contains(user)) {
            return;
        }

        ContactLink contactLink = new ContactLink();
        contactLink.setConnectionType(type);
        // Set link owner
        contactLink.setLinkOwnerUser(this);
        contactLink.setLinkOwnerUserDaoId(this.getId());
        // Set contact
        contactLink.setUser(user);
        contactLink.setUserId(user.getId());
        // insert contact link entity into DB
        daoSession.insertOrReplace(contactLink);

        ChatSDK.events().source().onNext(NetworkEvent.contactAdded(user));
    }

    public void deleteContact (User user) {
        deleteContact(user, ConnectionType.Contact);
    }

    public void deleteContact (User user, ConnectionType type) {

        Property [] properties = {
                ContactLinkDao.Properties.LinkOwnerUserDaoId,
                ContactLinkDao.Properties.UserId,
                ContactLinkDao.Properties.Type
        };

        List<ContactLink> contactLinks = DaoCore.fetchEntitiesWithProperties(ContactLink.class,
                properties, this.getId(), user.getId(), type.ordinal());

        for(ContactLink link : contactLinks) {
            ChatSDK.db().delete(link);
        }

        ChatSDK.events().source().onNext(NetworkEvent.contactDeleted(user));
    }

    public void addContact(User user) {
        addContact(user, ConnectionType.Contact);
    }

    public void setAvatarURL(String imageUrl, String hash) {
        setAvatarURL(imageUrl);
        setAvatarHash(hash);
    }

    public void setAvatarURL(String imageUrl) {
        setMetaString(Keys.AvatarURL, imageUrl);
    }

    public void setPresenceSubscription (String presence) {
        setMetaString(Keys.PresenceSubscription, presence);
    }

    public String getPresenceSubscription () {
        return metaStringForKey(Keys.PresenceSubscription);
    }

    public String getAvatarURL() {
        return metaStringForKey(Keys.AvatarURL);
    }

    public String getAvatarUrlOrDefault(Context context) {
        if (!StringChecker.isNullOrEmpty(getAvatarURL())) {
            return getAvatarURL();
        }
        return ImageUtils.uriForResourceId(context, ChatSDK.ui().getDefaultProfileImage()).toString();
    }

    public void setAvatarHash(String hash) {
        setMetaString(Keys.AvatarHash, hash);
    }

    public String getAvatarHash() {
        return metaStringForKey(Keys.AvatarHash);
    }

    public void setName(String name) {
        setMetaString(Keys.Name, name);
    }

    public String getName() {
        return metaStringForKey(Keys.Name);
    }

    public void setEmail(String email) {
        setMetaString(Keys.Email, email);
    }

    public String getEmail() {
        return metaStringForKey(Keys.Email);
    }

    public void setStatus (String status) {
        setMetaString(Keys.Status, status);
    }

    public String getStatus () {
        return metaStringForKey(Keys.Status);
    }

    public String getPhoneNumber () {
        return metaStringForKey(Keys.Phone);
    }

    public void setPhoneNumber (String phoneNumber) {
        setMetaString(Keys.Phone, phoneNumber);
    }

    public void setAvailability (String availability) {
        setMetaString(Keys.Availability, availability, false);
        ChatSDK.events().source().onNext(NetworkEvent.userPresenceUpdated(this));
    }

    public Boolean getIsOnline () {
        if (this.isOnline != null) {
            return this.isOnline;
        }
        return false;
    }

    public String getState () {
        return metaStringForKey(Keys.State);
    }

    public void setState (String state) {
        setMetaString(Keys.State, state);
    }

    public String getAvailability () {
        if (!getIsOnline()) {
            return null;
        }
//        String availability = metaStringForKey(Keys.Availability);
//        if (availability != null) {
//            return availability;
//        } else {
//            return Availability.Available;
//        }
        return metaStringForKey(Keys.Availability);
    }

    public String getLocation () {
        return metaStringForKey(Keys.Location);
    }

    public void setLocation (String location) {
        setMetaString(Keys.Location, location);
    }

    public String metaStringForKey(String key) {
        return (String) metaMap().get(key);
    }

    public Boolean metaBooleanForKey(String key) {
        return metaValueForKey(key) != null && metaValueForKey(key).getValue().toLowerCase().equals("true");
    }

    public void setMetaString(String key, String value) {
        setMetaValue(key, value, true);
    }

    public void setMetaString(String key, String value, boolean notify) {
        setMetaValue(key, value, notify);
    }

    /**
     * Setting the metaData, The Map will be converted to a Json String.
     **/
    public void setMetaMap(Map<String, String> metadata){
        for(String key : metadata.keySet()) {
            setMetaValue(key, metadata.get(key), false);
        }
        ChatSDK.events().source().onNext(NetworkEvent.userMetaUpdated(this));
    }

    /**
     * Converting the metaData json to a map object
     **/
    public Map<String, String> metaMap() {
        HashMap<String, String> map = new HashMap<>();

        for(UserMetaValue v : getMetaValues()) {
            map.put(v.getKey(), v.getValue());
        }

        return map;
    }

    public void setMetaValue (String key, String value) {
        setMetaValue(key, value, true);
    }

    @Keep
    public void setMetaValue (String key, String value, boolean notify) {
        UserMetaValue metaValue = metaValueForKey(key);
        if (metaValue == null) {
            metaValue = ChatSDK.db().createEntity(UserMetaValue.class);
            metaValue.setUserId(this.getId());
            getMetaValues().add(metaValue);
        }
        metaValue.setValue(value);
        metaValue.setKey(key);

        metaValue.update();
        update();

        if (notify) {
            ChatSDK.events().source().onNext(NetworkEvent.userMetaUpdated(this));
        }
    }

    @Keep
    public UserMetaValue metaValueForKey (String key) {
        ArrayList<MetaValue> values = new ArrayList<>();
        values.addAll(getMetaValues());
        return (UserMetaValue) MetaValueHelper.metaValueForKey(key, values);
    }

    public boolean hasThread(Thread thread){
        UserThreadLink data = DaoCore.fetchEntityWithProperties (
                UserThreadLink.class,
                new Property[] {UserThreadLinkDao.Properties.ThreadId, UserThreadLinkDao.Properties.UserId},
                thread.getId(),
                getId()
        );

        return data != null;
    }

    public String getPushChannel() {
        // Make the push channel safe
        String channel = entityID;
        channel = channel.replace(".", "1");
        channel = channel.replace("%2E", "1");
        channel = channel.replace("@", "2");
        channel = channel.replace("%40", "2");
        channel = channel.replace(":", "3");
        channel = channel.replace("%3A", "3");
        return channel;
    }

    public boolean isMe() {
        return equalsEntity(ChatSDK.currentUser());
    }

    public String toString() {
        return String.format("User, id: %s meta: %s", id, metaMap().toString());
    }

    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getEntityID() {
        return this.entityID;
    }


    public void setEntityID(String entityID) {
        this.entityID = entityID;
    }


    public Integer getAuthenticationType() {
        return this.authenticationType;
    }


    public void setAuthenticationType(Integer AuthenticationType) {
        this.authenticationType = AuthenticationType;
    }

    public java.util.Date getLastOnline() {
        return this.lastOnline;
    }


    public void setLastOnline(java.util.Date lastOnline) {
        this.lastOnline = lastOnline;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2059241980)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getUserDao() : null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1849453054)
    public List<UserMetaValue> getMetaValues() {
        if (metaValues == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserMetaValueDao targetDao = daoSession.getUserMetaValueDao();
            List<UserMetaValue> metaValuesNew = targetDao._queryUser_MetaValues(id);
            synchronized (this) {
                if (metaValues == null) {
                    metaValues = metaValuesNew;
                }
            }
        }
        return metaValues;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 365870950)
    public synchronized void resetMetaValues() {
        metaValues = null;
    }

    public void setIsOnline(Boolean isOnline) {
        this.isOnline = isOnline;
        update();
        ChatSDK.events().source().onNext(NetworkEvent.userPresenceUpdated(this));
    }

    public Disposable loadAvatar(ImageView imageView, int width, int height) {
        return UserImageBuilder.loadAvatar(this, imageView, width, height);
    }
    public Single<Bitmap> getAvatarBitmap(int width, int height) {
        return UserImageBuilder.getAvatarBitmap(this, width, height);
    }
}
