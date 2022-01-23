package com.application.cloudsek.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.application.cloudsek.R
import com.application.cloudsek.databinding.PostLayoutBinding
import com.application.cloudsek.model.Post
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class PostAdapter(
    private val mContext: Context,
    private val mPost: List<Post>,
) : RecyclerView.Adapter<PostAdapter.PostHolder>() {

    private var firebaseUser: FirebaseUser? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {

        val postLayoutBinding: PostLayoutBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.post_layout, parent, false)

        return PostHolder(postLayoutBinding)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val post = mPost[position]


        Glide.with(holder.postLayoutBinding.postImageHome).load(post.postImage).into(
            holder.postLayoutBinding.postImageHome)
        holder.postLayoutBinding.userNameSearch.text = post.publisherName

        publisherInfo(holder.postLayoutBinding.publisher, post.postid.toString())
        isLike(post.postid.toString(),holder.postLayoutBinding.postImageLikeBtn)
        nrlikes(holder.postLayoutBinding.postLikes,post.postid.toString())

        holder.postLayoutBinding.postImageLikeBtn.setOnClickListener {
            if(holder.postLayoutBinding.likes.getTag().equals("Like")){
                FirebaseDatabase.getInstance().getReference().child("Likes")
                    .child(post.postid.toString())
            }else{
                FirebaseDatabase.getInstance().getReference().child("Likes")
                    .child(post.postid.toString())
                    .child(firebaseUser?.uid!!).removeValue()
            }
        }
    }

    private fun publisherInfo(publisher: TextView, postPublisher: String) {

        val usersRef = FirebaseDatabase.getInstance().reference.child("Users")
            .child(postPublisher)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    fun isLike(postid: String, imageView: ImageView) {

        val firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference()
            .child("Likes")
            .child(postid)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(firebaseUser.uid).exists()) {
                    imageView.setImageResource(R.drawable.redhear)
                    imageView.setTag("Liked")
                } else {
                    imageView.setImageResource(R.drawable.heart_not_clicked)
                    imageView.setTag("Like")
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    fun nrlikes(likesimage: TextView, postid: String) {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference()
            .child("Likes")
            .child(postid)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                likesimage.setText(snapshot.childrenCount.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    class PostHolder(
        val postLayoutBinding: PostLayoutBinding,

    ) : RecyclerView.ViewHolder(postLayoutBinding.root) {
    }
}
