package com.meslmawy.messangerapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.meslmawy.messangerapp.R
import com.meslmawy.messangerapp.databinding.ActivityHomeBinding
import com.meslmawy.messangerapp.ui.login.LoginActivity
import java.util.*

class HomeActivity : AppCompatActivity() {

    private val viewModel: HomeViewModel by lazy {
        ViewModelProviders.of(this).get(HomeViewModel::class.java)
    }
    lateinit var binding: ActivityHomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        binding.viewmodel = viewModel
        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this
        getToolBarData()
        buildViewPager()
    }

    private fun getToolBarData(){
        val toolbar: androidx.appcompat.widget.Toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        viewModel.titleName.observe(this, Observer {
            it?.let {
                binding.username.text = it
            }
        })
        viewModel.imageUrl.observe(this, Observer {
            it?.let {
                if (it == "default")
                    binding.profileImage.setImageResource(R.mipmap.ic_launcher)
                else
                    Glide.with(applicationContext).load(it).into(binding.profileImage)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.status("online")
    }

    override fun onPause() {
        super.onPause()
        viewModel.status("offline")
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                val i = Intent(this, LoginActivity::class.java)
                startActivity(i)
                (this).overridePendingTransition(R.anim.slide_up, R.anim.slide_down)
                finish()
                return true
            }
        }
        return false
    }

    private fun buildViewPager(){
        val tabLayout: TabLayout = binding.tabLayout
        val viewPager: ViewPager = binding.viewPager
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        viewPagerAdapter.addFragment(ChatsFragment(), "Chats")
        viewPagerAdapter.addFragment(UsersFragment(), "Users")
        viewPagerAdapter.addFragment(ProfileFragment(), "Profile")
        viewPager.adapter = viewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)

        viewModel.getChats()

        viewModel.chatsNumber.observe(this, Observer {
            it?.let {

            }
        })

    }

    internal class ViewPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm!!) {
        private val fragments: ArrayList<Fragment> = ArrayList<Fragment>()
        private val titles: ArrayList<String> = ArrayList()

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }


        fun addFragment(fragment: Fragment, title: String) {
            fragments.add(fragment)
            titles.add(title)
        }

        // Ctrl + O
        @Nullable
        override fun getPageTitle(position: Int): CharSequence {
            return titles[position]
        }

    }

}