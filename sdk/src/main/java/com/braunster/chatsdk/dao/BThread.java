package com.braunster.chatsdk.dao;

import android.util.Log;

import com.braunster.chatsdk.Utils.Debug;
import com.braunster.chatsdk.Utils.sorter.MessageSorter;
import com.braunster.chatsdk.dao.core.DaoCore;
import com.braunster.chatsdk.dao.entities.BThreadEntity;
import com.braunster.chatsdk.dao.entities.Entity;
import com.braunster.chatsdk.network.BDefines;
import com.braunster.chatsdk.network.BNetworkManager;
import com.braunster.chatsdk.network.firebase.BFirebaseDefines;
import com.braunster.chatsdk.network.firebase.BPath;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.dao.DaoException;
import de.greenrobot.dao.Property;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS
// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table BTHREAD.
 */
public class BThread extends BThreadEntity  {

    private Long id;
    private String entityID;
    private java.util.Date creationDate;
    private Boolean dirty;
    private Boolean hasUnreadMessages;
    private String name;
    private java.util.Date LastMessageAdded;
    private Integer type;
    private Long creator_ID;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient BThreadDao myDao;

    private BUser creator;
    private Long creator__resolvedKey;

    private List<BMessage> messages;
    private List<BLinkData> BLinkData;

    // KEEP FIELDS - put your custom fields here


    public static final String TAG = BThread.class.getSimpleName();
    public static final boolean DEBUG = Debug.BThread;
    // KEEP FIELDS END

    public BThread() {
    }

    public BThread(Long id) {
        this.id = id;
    }

    public BThread(Long id, String entityID, java.util.Date creationDate, Boolean dirty, Boolean hasUnreadMessages, String name, java.util.Date LastMessageAdded, Integer type, Long creator_ID) {
        this.id = id;
        this.entityID = entityID;
        this.creationDate = creationDate;
        this.dirty = dirty;
        this.hasUnreadMessages = hasUnreadMessages;
        this.name = name;
        this.LastMessageAdded = LastMessageAdded;
        this.type = type;
        this.creator_ID = creator_ID;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getBThreadDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntityID() {
        return entityID;
    }

    public void setEntityID(String entityID) {
        this.entityID = entityID;
    }

    public java.util.Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(java.util.Date creationDate) {
        this.creationDate = creationDate;
    }

    public Boolean getDirty() {
        return dirty;
    }

    public void setDirty(Boolean dirty) {
        this.dirty = dirty;
    }

    public Boolean getHasUnreadMessages() {
        return hasUnreadMessages;
    }

    public void setHasUnreadMessages(Boolean hasUnreadMessages) {
        this.hasUnreadMessages = hasUnreadMessages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public java.util.Date getLastMessageAdded() {
        return LastMessageAdded;
    }

    public void setLastMessageAdded(java.util.Date LastMessageAdded) {
        this.LastMessageAdded = LastMessageAdded;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getCreator_ID() {
        return creator_ID;
    }

    public void setCreator_ID(Long creator_ID) {
        this.creator_ID = creator_ID;
    }

    /** To-one relationship, resolved on first access. */
    public BUser getCreator() {
        Long __key = this.creator_ID;
        if (creator__resolvedKey == null || !creator__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            BUserDao targetDao = daoSession.getBUserDao();
            BUser creatorNew = targetDao.load(__key);
            synchronized (this) {
                creator = creatorNew;
            	creator__resolvedKey = __key;
            }
        }
        return creator;
    }

    public void setCreator(BUser creator) {
        synchronized (this) {
            this.creator = creator;
            creator_ID = creator == null ? null : creator.getId();
            creator__resolvedKey = creator_ID;
        }
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<BMessage> getMessages() {
        if (messages == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            BMessageDao targetDao = daoSession.getBMessageDao();
            List<BMessage> messagesNew = targetDao._queryBThread_Messages(id);
            synchronized (this) {
                if(messages == null) {
                    messages = messagesNew;
                }
            }
        }
        return messages;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetMessages() {
        messages = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<BLinkData> getBLinkData() {
        if (BLinkData == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            BLinkDataDao targetDao = daoSession.getBLinkDataDao();
            List<BLinkData> BLinkDataNew = targetDao._queryBThread_BLinkData(id);
            synchronized (this) {
                if(BLinkData == null) {
                    BLinkData = BLinkDataNew;
                }
            }
        }
        return BLinkData;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetBLinkData() {
        BLinkData = null;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

    // KEEP METHODS - put your custom methods here
    @Override
    public BPath getPath() {
        return new BPath().addPathComponent(BFirebaseDefines.Path.BThreadPath, getEntityID());}

    @Override
    public Entity.Type getEntityType() {
        return com.braunster.chatsdk.dao.entity_interface.Entity.Type.bEntityTypeThread;
    }

    @Override
    public void updateFromMap(Map<String, Object> map) {
        if (DEBUG) Log.d(TAG, "Update from map.");

        if (map == null) return;

        if (map.containsKey(BDefines.Keys.BCreationDate))
        {
            Long data = (Long) map.get(BDefines.Keys.BCreationDate);
            if (data != null && data > 0)
                creationDate = new Date(/*Double.valueOf(data).longValue()*/ data);
        }

        long type;
        if (map.containsKey(BDefines.Keys.BType))
        {
            type = (Long) map.get(BDefines.Keys.BType);
            this.type = (int) type;
            if (DEBUG) Log.d(TAG, "Setting type to: " + this.type);
        }

        if (map.containsKey(BDefines.Keys.BName) && !map.get(BDefines.Keys.BName).equals(""))
            name = (String) map.get(BDefines.Keys.BName);

        Long lastMessageAdded = (Long) map.get(BDefines.Keys.BLastMessageAdded);
        if (lastMessageAdded != null && lastMessageAdded > 0)
        {
            Date date = new Date(lastMessageAdded);
            if (LastMessageAdded == null || date.getTime() > LastMessageAdded.getTime())
                LastMessageAdded = date;
        }

    }

    @Override
    public Map<String, Object> asMap() {
        Map<String , Object> map = new HashMap<String, Object>();
        Map<String , Object> nestedMap = new HashMap<String, Object>();

        nestedMap.put(BDefines.Keys.BCreationDate, creationDate.getTime());
        nestedMap.put(BDefines.Keys.BName, name);
        nestedMap.put(BDefines.Keys.BType, type);

        // TODO get the date from the message list.
        if (LastMessageAdded != null)
            nestedMap.put(BDefines.Keys.BLastMessageAdded, LastMessageAdded.getTime());

        map.put(BFirebaseDefines.Path.BDetailsPath, nestedMap);

        return map;
    }

    @Override
    public Object getPriority() {
        return null;
    }

    public void setMessages(List<BMessage> messages) {
        this.messages = messages;
    }

    public List<BUser> getUsers(){
        /* Getting the users list by getBLinkData can be out of date so we get the data from the database*/

        List<BLinkData> list =  DaoCore.fetchEntitiesWithProperty(BLinkData.class, BLinkDataDao.Properties.ThreadID, getId());

        if (DEBUG) Log.d(TAG, "BThread, getUsers, Amount: " + (list == null ? "null" : list.size()));

        List<BUser> users  = new ArrayList<BUser>();

        if (list == null) {
            return users;
        }

        for (BLinkData data : list)
            if (data.getBUser() != null && !users.contains(data.getBUser()))
                users.add(data.getBUser());

        return users;
    }

    public String displayName(){
        return displayName(getUsers());
    }

    public String displayName(List<BUser> users){
        if (DEBUG) Log.v(TAG, "displayName");
        if (type == null)
            return name;

        if (BNetworkManager.sharedManager().getNetworkAdapter() == null)
            return name;

        // Due to the data printing when the app run on debug this sometime is null.
        BUser curUser = BNetworkManager.sharedManager().getNetworkAdapter().currentUser();

        if (type != Type.Public){
            String name = "";

            for (BUser user : getUsers()){
                if (!user.getId().equals(curUser.getId()))
                {
                    String n = user.getMetaName();

                    if (StringUtils.isNotEmpty(n)) {
                        if (DEBUG) Log.d(TAG, "User name: " + n);
                        name += (!name.equals("") ? ", " : "") + n;
                    }
                }
            }

            return name;
        }
        else if (type == BThreadEntity.Type.Public)
            return name;

        return "No name available...";
    }

    //ASK do we save last message added value in the db or we just calc it by the message date.
    public Date lastMessageAdded(){
        // ASK what to do when there is no creation date like when i get a public thread.
        Date date = creationDate;

        List<BMessage> list =getMessagesWithOrder(DaoCore.ORDER_DESC);

        if (list.size() > 0)
            date = list.get(0).getDate();

        if (date == null)
            date = new Date();//FIXME remove this thing

        return date;
    }

    /** Fetch messages list from the db for current thread, Messages will be order Desc/Asc on demand.*/
    public List<BMessage> getMessagesWithOrder(int order){

        List<BMessage> list = DaoCore.fetchEntitiesWithProperty(BMessage.class, BMessageDao.Properties.OwnerThread, getId());

        Collections.sort(list, new MessageSorter(order));

        return list;
    }

    public boolean hasUser(BUser user){


        com.braunster.chatsdk.dao.BLinkData data =
                DaoCore.fetchEntityWithProperties(com.braunster.chatsdk.dao.BLinkData.class,
                        new Property[]{BLinkDataDao.Properties.ThreadID, BLinkDataDao.Properties.UserID}, getId(), user.getId());

/*        for (BUser u : getUsers())
        {
            if (u.getId().longValue() ==  user.getId().longValue())
               return true;
        }*/

        return data != null;
    }

    public int getUnreadMessagesAmount(){
        int count = 0;
        List<BMessage> messages = getMessagesWithOrder(DaoCore.ORDER_DESC);
        for (BMessage m : messages)
        {
            if(!m.wasRead())
                count++;
            else break;
        }

        return count;
    }

    public boolean isLastMessageWasRead(){
        List<BMessage> messages = getMessagesWithOrder(DaoCore.ORDER_DESC);
        return messages == null || messages.size() == 0 || getMessagesWithOrder(DaoCore.ORDER_DESC).get(0).wasRead();
    }
    // KEEP METHODS END

}