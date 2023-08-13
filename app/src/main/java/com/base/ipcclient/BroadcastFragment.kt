package com.base.ipcclient

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.base.ipcclient.Constants.DATA
import com.base.ipcclient.Constants.PACKAGE_NAME
import com.base.ipcclient.Constants.PID
import com.base.ipcclient.databinding.FragmentBroadcastBinding
import java.util.Calendar

class BroadcastFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentBroadcastBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBroadcastBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.btnConnect.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {

        sendBroadCast()

        showBroadCastTime()
    }
    private fun sendBroadCast(){
        val intent = Intent()
        intent.action = "com.base.ipcserver"
        intent.putExtra(PACKAGE_NAME,context?.packageName)
        intent.putExtra(PID,Process.myPid().toString())
        intent.putExtra(DATA,binding.edtClientData.text.toString())
        intent.component = ComponentName("com.base.ipcserver","com.base.ipcserver.IPCBroadCastReceiver")
        activity?.applicationContext?.sendBroadcast(intent)

    }
    private fun showBroadCastTime(){
        val cal = Calendar.getInstance()
        val time = "${cal.get(Calendar.HOUR)}:${cal.get(Calendar.MINUTE)}:${cal.get(Calendar.SECOND)}"
        binding.linearLayoutClientInfo.visibility = View.VISIBLE
        binding.txtDate.text = time
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}