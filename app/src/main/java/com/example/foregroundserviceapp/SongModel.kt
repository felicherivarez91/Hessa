package com.example.foregroundserviceapp

 class SongModel (
    private var id : Int,
    private var name : String,
    private var resourceId : Int,
    private var image : Int
        ){

    fun getId() : Int{
        return id
    }

    fun setId(id: Int){
        this.id = id
    }

    fun getName() : String{
        return name
    }

    fun setName(name: String){
        this.name = name
    }
    fun getImage(): Int{
        return image
    }

    fun setImage(image: Int){
        this.image = image
    }
    fun getResourceId() : Int{
        return resourceId
    }
    fun setResourceId(resourceId: Int){
        this.resourceId = resourceId
    }

}


