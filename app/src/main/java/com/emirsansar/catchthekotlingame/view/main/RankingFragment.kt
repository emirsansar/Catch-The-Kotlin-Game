package com.emirsansar.catchthekotlingame.view.main

import android.graphics.PorterDuff
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.emirsansar.catchthekotlingame.R
import com.emirsansar.catchthekotlingame.adapter.RankAdapter
import com.emirsansar.catchthekotlingame.databinding.FragmentRankingBinding
import com.emirsansar.catchthekotlingame.model.Rank
import com.emirsansar.catchthekotlingame.viewmodel.RankingViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage

class RankingFragment : Fragment() {

    private lateinit var binding: FragmentRankingBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var currentUser : FirebaseUser? = null
    private lateinit var storage: FirebaseStorage

    private var rankAdapter: RankAdapter? = null

    private var lastSelectedSecond: String = "0"

    private var colorActive: Int? = null
    private var colorDefault: Int? = null

    private lateinit var viewModel : RankingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firestore = Firebase.firestore
        storage = Firebase.storage
        auth = Firebase.auth
        currentUser = auth.currentUser

        colorActive = ContextCompat.getColor(requireActivity(), R.color.btn_active)
        colorDefault = ContextCompat.getColor(requireActivity(), R.color.btn_default)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRankingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[RankingViewModel::class.java]

        setRecyclerView()

        setButtonListeners()
        setSwipeRefreshLayoutListener()
    }


    private fun setRecyclerView(){
        binding.recyclerRankView.layoutManager = LinearLayoutManager(context)

        rankAdapter = RankAdapter(arrayListOf(), storage, firestore)
        binding.recyclerRankView.adapter = rankAdapter
    }

    private fun setSwipeRefreshLayoutListener(){
        binding.swipeRefreshLayout.setOnRefreshListener {
            if (lastSelectedSecond.toInt() != 0)
                viewModel.getRankingList(lastSelectedSecond) { rankingList ->
                    rankAdapter!!.updateData(rankingList) }

            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setButtonListeners() {
        binding.btn10Second.setOnClickListener {
            changeButtonTint(binding.btn10Second, colorActive!!)
            resetButtonTint(binding.btn10Second)
            lastSelectedSecond = "10"
            viewModel.getRankingList("10") {
                rankAdapter?.updateData(it)
                showRecyclerView()
            }
        }

        binding.btn30Second.setOnClickListener {
            changeButtonTint(binding.btn30Second, colorActive!!)
            resetButtonTint(binding.btn30Second)
            lastSelectedSecond = "30"
            viewModel.getRankingList("30") {
                rankAdapter?.updateData(it)
                showRecyclerView()
            }
        }

        binding.btn60Second.setOnClickListener {
            changeButtonTint(binding.btn60Second, colorActive!!)
            resetButtonTint(binding.btn60Second)
            lastSelectedSecond = "60"
            viewModel.getRankingList("60") {
                rankAdapter?.updateData(it)
                showRecyclerView()
            }
        }
    }

    private fun changeButtonTint(clickedButton: Button, color: Int) {
        val drawable = clickedButton.background
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_ATOP)
        DrawableCompat.setTint(drawable, color)

        val buttons = listOf(binding.btn10Second, binding.btn30Second, binding.btn60Second)
        for (button in buttons) {
            if (button == clickedButton) {
                button.setTextColor(resources.getColor(R.color.black))
                button.textSize = 16f
            }
        }
    }

    private fun resetButtonTint(clickedButton: Button) {
        val buttons = listOf(binding.btn10Second, binding.btn30Second, binding.btn60Second)
        for (button in buttons) {
            if (button != clickedButton) {
                changeButtonTint(button, colorDefault!!)
                button.setTextColor(resources.getColor(R.color.white))
                button.textSize = 14f
            }
        }
    }

    private fun showRecyclerView(){
        binding.textNoChoice.visibility = View.GONE
        binding.swipeRefreshLayout.visibility = View.VISIBLE
    }
}