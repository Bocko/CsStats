package hu.bme.aut.android.stats.menu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import hu.bme.aut.android.stats.databinding.ActivityAddPlayerBinding

class AddPlayerActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddPlayerBinding

    companion object {
        private const val TAG = "DetailsActivity"
        const val PLAYER_NAME = "player_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAdd.setOnClickListener {
            val id = Intent()
            id.putExtra("player",binding.etnewPlayer.text.toString())
            this.setResult(1,id)
            this.finish()
        }
        binding.btnCancel.setOnClickListener {
            this.finish()
        }
    }


}