package edu.upc.dsa.dsa_error404_android;

public class CompraRequest {
    private String userName;
    private String ItemName;

    public CompraRequest(String userName, String ItemName) {
        this.userName = userName;
        this.ItemName = ItemName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getItemName (){
        return ItemName;
    }

    public void setItemName(String itemName){
        this.ItemName = itemName;
    }
}

