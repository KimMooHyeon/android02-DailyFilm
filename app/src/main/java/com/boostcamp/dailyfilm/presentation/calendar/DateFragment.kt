package com.boostcamp.dailyfilm.presentation.calendar

import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.databinding.FragmentDateBinding
import com.boostcamp.dailyfilm.presentation.BaseFragment
import com.boostcamp.dailyfilm.presentation.calendar.adpater.CalendarAdapter
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class DateFragment(val onUploadFilm: (DateModel?) -> Unit) :
    BaseFragment<FragmentDateBinding>(R.layout.fragment_date) {

    private val viewModel: DateViewModel by viewModels()
    private lateinit var adapter: CalendarAdapter

    override fun initView() {
        initAdapter()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dateFlow.collectLatest {
                    adapter.submitList(it)
                }
            }
        }
    }

    private fun initAdapter() {
        adapter = CalendarAdapter(
            viewModel.calendar,
            Glide.with(this),
            { dateModel ->
                onUploadFilm(null)
                Toast.makeText(requireContext(), "img", Toast.LENGTH_SHORT).show()
            },
            { dateModel ->
                onUploadFilm(dateModel)
                Toast.makeText(requireContext(), "$dateModel", Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvCalendar.adapter = adapter
        binding.rvCalendar.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL).apply {
                ContextCompat.getDrawable(requireContext(), R.drawable.div_calendar_week)?.let {
                    setDrawable(it)
                }
            }
        )
    }

    companion object {
        const val KEY_CALENDAR = "calendar"
        fun newInstance(calendar: Calendar, lambda: (DateModel?) -> Unit): DateFragment {
            return DateFragment(lambda).apply {
                arguments = Bundle().apply {
                    putSerializable(KEY_CALENDAR, calendar)
                }
            }
        }
    }
}