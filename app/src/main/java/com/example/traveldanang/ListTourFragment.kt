package com.example.traveldanang

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ListTourFragment : Fragment() {
    private lateinit var listId: Array<Array<Int>>
    val db = FirebaseFirestore.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_alltours, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val txtSearch = view.findViewById<EditText>(R.id.txtSearch)
        txtSearch.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                search(view)
                val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                return@setOnEditorActionListener true
            }
            false
        }
        val searchBtn = view.findViewById<ImageButton>(R.id.searchBtn)
        searchBtn.setOnClickListener {
            search(view)
            val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        db.collection("tours").limit(10).get().addOnSuccessListener {
            val tourDatas = ArrayList<Tour>()
            for (tour in it) {
                val data = Tour(tour.id, tour.get("details"),
                    tour.get("images") as ArrayList<String>, tour.get("infor"), tour.get("location"), tour.get("name"),
                    tour.get("price") as Number?, tour.get("type"), tour.get("sold"))
                tourDatas.add(data)
            }
            val rv = view.findViewById<RecyclerView>(R.id.recycleViewListTour)
            val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            rv.layoutManager = linearLayoutManager
            rv.adapter = TourAdapter(requireContext(), tourDatas)
        }
        val idLake = arrayOf(R.id.lakeFilter, R.id.cardViewLake, R.id.imageLake, R.drawable.lake, R.drawable.lake_primary)
        val idSea = arrayOf(R.id.seaFilter, R.id.cardViewSea, R.id.imageSea, R.drawable.sea, R.drawable.sea_primary)
        val idMountain = arrayOf(R.id.mountainFilter, R.id.cardViewMountain, R.id.imageMountain, R.drawable.mountain, R.drawable.mountain_primary)
        val idForest = arrayOf(R.id.forestFilter, R.id.cardViewForest, R.id.imageForest, R.drawable.forest, R.drawable.forest_primary)
        listId = arrayOf(idLake, idSea, idMountain, idForest)
        val lakeFilter = view.findViewById<LinearLayout>(R.id.lakeFilter)
        lakeFilter.setOnClickListener {
            setFilter(view, 0)
        }
        val seaFilter = view.findViewById<LinearLayout>(R.id.seaFilter)
        seaFilter.setOnClickListener {
            setFilter(view, 1)
        }
        val mountainFilter = view.findViewById<LinearLayout>(R.id.mountainFilter)
        mountainFilter.setOnClickListener {
            setFilter(view, 2)
        }
        val forestFilter = view.findViewById<LinearLayout>(R.id.forestFilter)
        forestFilter.setOnClickListener {
            setFilter(view, 3)
        }
    }

    private fun setFilter(view: View, index: Int){
        listId.forEachIndexed { i, filter ->
            val objectFilter = view.findViewById<LinearLayout>(filter.get(0));
            if (i == index) {
                val cardView = view.findViewById<CardView>(filter.get(1))
                val currentColor = cardView.cardBackgroundColor.defaultColor
                if (currentColor == ContextCompat.getColor(requireContext(), R.color.primary)) {
                    objectFilter.setBackgroundResource(R.drawable.card_view_no_border)
                    cardView.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                    val image = view.findViewById<ImageView>(filter.get(2))
                    image.setImageDrawable(ContextCompat.getDrawable(requireContext(), filter.get(4)))
                    db.collection("tours").limit(10).get().addOnSuccessListener {
                        val tourDatas = ArrayList<Tour>()
                        for (tour in it) {
                            val data = Tour(tour.id, tour.get("details"),
                                tour.get("images") as ArrayList<String>, tour.get("infor"), tour.get("location"), tour.get("name"),
                                tour.get("price") as Number?, tour.get("type"), tour.get("sold"))
                            tourDatas.add(data)
                        }
                        val rv = view.findViewById<RecyclerView>(R.id.recycleViewListTour)
                        val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                        rv.layoutManager = linearLayoutManager
                        rv.adapter = TourAdapter(requireContext(), tourDatas)
                    }
                }
                else {
                    objectFilter.setBackgroundResource(R.drawable.card_view_border)
                    cardView.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary))
                    val image = view.findViewById<ImageView>(filter.get(2))
                    image.setImageDrawable(ContextCompat.getDrawable(requireContext(), filter.get(3)))
                    db.collection("tours").whereEqualTo("type", index.toString()).limit(10).get().addOnSuccessListener {
                        val tourDatas = ArrayList<Tour>()
                        for (tour in it) {
                            val data = Tour(tour.id, tour.get("details"),
                                tour.get("images") as ArrayList<String>, tour.get("infor"), tour.get("location"), tour.get("name"),
                                tour.get("price") as Number?, tour.get("type"), tour.get("sold"))
                            tourDatas.add(data)
                        }
                        val rv = view.findViewById<RecyclerView>(R.id.recycleViewListTour)
                        val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                        rv.layoutManager = linearLayoutManager
                        rv.adapter = TourAdapter(requireContext(), tourDatas)
                    }
                }
            }
            else {
                objectFilter.setBackgroundResource(R.drawable.card_view_no_border)
                val cardView = view.findViewById<CardView>(filter.get(1))
                cardView.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                val image = view.findViewById<ImageView>(filter.get(2))
                image.setImageDrawable(ContextCompat.getDrawable(requireContext(), filter.get(4)))
            }
        }
    }

    private fun search(view: View) {
        val txtSearch = view.findViewById<EditText>(R.id.txtSearch)
        if (txtSearch.text.toString() != "" ) {
            db.collection("tours").whereGreaterThanOrEqualTo("name", txtSearch.text.toString()).whereLessThanOrEqualTo("name", txtSearch.text.toString() + "\uf8ff")
                .get().addOnSuccessListener {
                    val tourDatas = ArrayList<Tour>()
                    for (tour in it) {
                        val data = Tour(tour.id, tour.get("details"),
                            tour.get("images") as ArrayList<String>, tour.get("infor"), tour.get("location"), tour.get("name"),
                            tour.get("price") as Number?, tour.get("type"), tour.get("sold"))
                        tourDatas.add(data)
                    }
                    val rv = view.findViewById<RecyclerView>(R.id.recycleViewListTour)
                    val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                    rv.layoutManager = linearLayoutManager
                    rv.adapter = TourAdapter(requireContext(), tourDatas)
                }
        }
    }
}