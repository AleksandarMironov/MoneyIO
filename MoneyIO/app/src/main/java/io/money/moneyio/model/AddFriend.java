package io.money.moneyio.model;

public class AddFriend {

    private String myUid;
    private String myEmail;

    public AddFriend() {}

    public AddFriend(String myUid, String myEmail) {
        this.myUid = myUid;
        this.myEmail = myEmail;
    }

    public String getMyUid() {
        return myUid;
    }

    public String getMyEmail() {
        return myEmail;
    }
}
