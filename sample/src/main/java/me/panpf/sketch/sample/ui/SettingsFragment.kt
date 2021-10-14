package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.panpf.assemblyadapter.recycler.AssemblyRecyclerAdapter
import me.panpf.sketch.SLog
import me.panpf.sketch.sample.AppConfig
import me.panpf.sketch.sample.base.BaseToolbarFragment
import me.panpf.sketch.sample.databinding.FragmentRecyclerBinding
import me.panpf.sketch.sample.item.CheckMenuItemFactory
import me.panpf.sketch.sample.item.InfoMenuItemFactory
import me.panpf.sketch.sample.item.ListSeparatorItemFactory
import me.panpf.sketch.sample.vm.SettingsViewModel

class SettingsFragment : BaseToolbarFragment<FragmentRecyclerBinding>() {

    private val viewModel by viewModels<SettingsViewModel>()

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentRecyclerBinding.inflate(inflater, parent, false)

    override fun onInitData(
        toolbar: Toolbar,
        binding: FragmentRecyclerBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "Settings"

        binding.refreshRecyclerFragment.isEnabled = false

        binding.hintRecyclerFragment.hidden()

        binding.recyclerRecyclerFragmentContent.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = AssemblyRecyclerAdapter<Any>(
                listOf(
                    ListSeparatorItemFactory(),
                    CheckMenuItemFactory {
                        viewModel.update()
                    },
                    InfoMenuItemFactory {
                        viewModel.update()
                    }
                )
            ).apply {
                viewModel.menuListData.observe(viewLifecycleOwner) {
                    submitList(it)
                }
            }
        }

        viewModel.showLogLevelDialogEvent.listen(viewLifecycleOwner) {
            val appContext = requireContext()
            AlertDialog.Builder(requireActivity()).apply {
                setTitle("Switch Log Level")
                val items = arrayOf(
                    "VERBOSE" + if (SLog.getLevel() == SLog.VERBOSE) " (*)" else "",
                    "DEBUG" + if (SLog.getLevel() == SLog.DEBUG) " (*)" else "",
                    "INFO" + if (SLog.getLevel() == SLog.INFO) " (*)" else "",
                    "WARNING" + if (SLog.getLevel() == SLog.WARNING) " (*)" else "",
                    "ERROR" + if (SLog.getLevel() == SLog.ERROR) " (*)" else "",
                    "NONE" + if (SLog.getLevel() == SLog.NONE) " (*)" else ""
                )
                setItems(items) { _, which ->
                    when (which) {
                        0 -> AppConfig.putString(
                            appContext,
                            AppConfig.Key.LOG_LEVEL,
                            "VERBOSE"
                        )
                        1 -> AppConfig.putString(
                            appContext,
                            AppConfig.Key.LOG_LEVEL,
                            "DEBUG"
                        )
                        2 -> AppConfig.putString(
                            appContext,
                            AppConfig.Key.LOG_LEVEL,
                            "INFO"
                        )
                        3 -> AppConfig.putString(
                            appContext,
                            AppConfig.Key.LOG_LEVEL,
                            "WARNING"
                        )
                        4 -> AppConfig.putString(
                            appContext,
                            AppConfig.Key.LOG_LEVEL,
                            "ERROR"
                        )
                        5 -> AppConfig.putString(
                            appContext,
                            AppConfig.Key.LOG_LEVEL,
                            "NONE"
                        )
                    }
                    viewModel.update()
                }
                setPositiveButton("Cancel", null)
            }.show()
        }
    }
}