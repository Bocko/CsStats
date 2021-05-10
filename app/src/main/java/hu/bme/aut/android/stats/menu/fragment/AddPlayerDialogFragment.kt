package hu.bme.aut.android.stats.menu.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import hu.bme.aut.android.stats.R
import hu.bme.aut.android.stats.databinding.DialogNewPlayerBinding

class AddPlayerDialogFragment: AppCompatDialogFragment() {

    private var _binding: DialogNewPlayerBinding? = null
    private val binding get() = _binding!!

    private lateinit var listener: AddPlayerDialogListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = if (targetFragment != null) {
                targetFragment as AddPlayerDialogListener
            } else {
                activity as AddPlayerDialogListener
            }
        } catch (e: ClassCastException) {
            throw RuntimeException(e)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogNewPlayerBinding.inflate(LayoutInflater.from(context))
        return AlertDialog.Builder(requireContext())
                .setTitle(R.string.new_player)
                .setView(binding.root)
                .setPositiveButton(R.string.ok) { dialogInterface, _ ->
                    listener.onPlayerAdded(binding.newPlayerDialogEditText.text.toString())
                }
                .setNegativeButton(R.string.cancel, null)
                .create()
    }

    interface AddPlayerDialogListener{
        fun onPlayerAdded(idOrUrl: String)
    }

}