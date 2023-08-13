package com.base.ipcclient

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.base.ipcclient.databinding.FragmentAidlBinding
import android.os.Process
import android.os.RemoteException
import androidx.fragment.app.FragmentManager
import com.base.ipcclient.Constants.CONNECTION_COUNT
import com.base.ipcclient.Constants.DATA
import com.base.ipcclient.Constants.PACKAGE_NAME
import com.base.ipcclient.Constants.PID
import com.base.ipcclient.databinding.FragmentMessengerBinding
import com.base.ipcserver.IIPCExample

class MessengerFragment : Fragment(), ServiceConnection, View.OnClickListener {

    private var _binding: FragmentMessengerBinding? = null
    private val binding get() = _binding!!
    var iRemoteService: IIPCExample? = null
    private var connected = false


    private var serverMessenger:Messenger? = null
    private var clientMessenger:Messenger? = null


    var handler:Handler = object :Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val bundle = msg.data
            binding.apply {
                linearLayoutClientInfo.visibility = View.VISIBLE
                btnConnect.text = getString(R.string.disconnect)
                txtServerPid.text = bundle.getInt(PID).toString()
                txtServerConnectionCount.text = bundle.getInt(CONNECTION_COUNT).toString()
            }

        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMessengerBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.btnConnect.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    var isBound = false
    override fun onClick(v: View?) {
       if (isBound){
           doUnBindService()
       }else{
           doBindService()
       }
    }

    private fun doBindService() {
        clientMessenger = Messenger(handler)
        Intent("messengerexample").also {
            it.`package` = "com.base.ipcserver"
            activity?.applicationContext?.bindService(it,this,Context.BIND_AUTO_CREATE)
        }
        isBound = true
    }

    private fun doUnBindService() {
       if (isBound){
           activity?.applicationContext?.unbindService(this)
           isBound = false
       }
    }


    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        serverMessenger = Messenger(service)
        // we are ready to send remote message to remote service
        sendMessageToServer()

    }


    private fun sendMessageToServer() {
        if (!isBound) return
        val message = Message.obtain(handler)
        val bundle = Bundle()
        bundle.putString(DATA,binding.edtClientData.text.toString())
        bundle.putString(PACKAGE_NAME, context?.packageName)
        bundle.putInt(PID,Process.myPid())
        message.data = bundle
        message.replyTo = clientMessenger  // we offer our Messenger object for communication to be two-way
        try {
            serverMessenger?.send(message)
        }catch (ex:RemoteException){
            ex.printStackTrace()
        }finally {
            message.recycle()
        }
    }
    private fun clearUI(){
        binding.txtServerPid.text = ""
        binding.txtServerConnectionCount.text = ""
        binding.btnConnect.text = getString(R.string.connect)
        binding.linearLayoutClientInfo.visibility = View.INVISIBLE
    }
    override fun onServiceDisconnected(name: ComponentName?) {
        clearUI()
        serverMessenger = null
    }
}