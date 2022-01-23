package com.application.cloudsek.extras

import androidx.lifecycle.MutableLiveData
import com.application.cloudsek.model.Post

class FirebaseLiveDataList {

    companion object{
        var livedata = MutableLiveData<List<Post>>()
    }

}