package com.application.cloudsek.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.cloudsek.R
import com.application.cloudsek.adapter.PostAdapter
import com.application.cloudsek.model.Post
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment(R.layout.fragment_home) {

    private var postAdapter: PostAdapter? = null
    private var postList = mutableListOf<Post>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        retirvePosts()
    }

    private fun retirvePosts() {
        val postRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postList.clear()

                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val postFromFireBase = userSnapshot.getValue(Post::class.java)
                        postList.add(postFromFireBase!!)

                        val linearLayoutManager = LinearLayoutManager(context)
                        linearLayoutManager.reverseLayout = true
                        linearLayoutManager.stackFromEnd = true
                        RecyclerView_Home.layoutManager = linearLayoutManager


                        postAdapter = this?.let { PostAdapter(context!!, postList) }
                       RecyclerView_Home.adapter = postAdapter
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}