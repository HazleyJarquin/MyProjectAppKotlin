package com.example.myprojectapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myprojectapplication.database.MyAppDatabase
import com.example.myprojectapplication.database.entity.UsersEntity
import com.example.myprojectapplication.databinding.ActivityViewUsersBinding
import com.example.myprojectapplication.utils.showAlertDialog
import com.google.android.material.textview.MaterialTextView
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ViewUsersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewUsersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val myAppDb = (application as MyAppRoomApplication).myAppDb
        binding.apply {
            toolbar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()

            }

            lifecycleScope.launch {
                try {
                    val customers = withContext(Dispatchers.IO) {
                        myAppDb.usersDao().getAllUsers()
                    }

                    val adapter = UsersAdapter(customers)
                    recyclerViewUsers.layoutManager = LinearLayoutManager(this@ViewUsersActivity)
                    recyclerViewUsers.adapter = adapter

                } catch (e: Exception) {
                    e.printStackTrace()
                    showAlertDialog(
                        "Error",
                        "Error al cargar los usuarios",
                        "OK",
                        this@ViewUsersActivity
                    )
                }
            }
        }
    }

    class UsersAdapter(private val users: List<UsersEntity>) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
            return UserViewHolder(view)
        }

        override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
            val user = users[position]
            holder.usernameTxt.text = "Username: ${user.userName}"
            holder.nameTxt.text = "Nombre: ${user.name}"
            holder.cardView.setOnClickListener{
                val intent = Intent(it.context, ViewUserByIdActivity::class.java)
                intent.putExtras(Bundle().apply {
                    putInt("id", user.id)
                })
                it.context.startActivity(intent)

            }
        }

        override fun getItemCount(): Int {
            return users.size
        }

        class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val usernameTxt: MaterialTextView = view.findViewById(R.id.usernameTxt)
            val nameTxt: MaterialTextView = view.findViewById(R.id.nameTxt)
            val cardView: MaterialCardView = view.findViewById(R.id.cardView)

        }
    }
}
