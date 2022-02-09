package kz.tengrilab.fr_cppr.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kz.tengrilab.fr_cppr.Variables
import kz.tengrilab.fr_cppr.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.nameText.text = loadUsername()
        binding.photoLayout.setOnClickListener {
            MainFragmentDirections.actionConnectImageFr().apply {
                findNavController().navigate(this)
            }
        }

        binding.galleryLayout.setOnClickListener {
            MainFragmentDirections.actionConnectGalleryFr().apply {
                findNavController().navigate(this)
            }
        }

        binding.resultLayout.setOnClickListener {
            MainFragmentDirections.actionConnectResultsFr().apply {
                findNavController().navigate(this)
            }
        }

        binding.carLayout.setOnClickListener {
            MainFragmentDirections.actionConnectCarnumberFr().apply {
                findNavController().navigate(this)
            }
        }
        binding.settingsButton.setOnClickListener {
            MainFragmentDirections.actionConnectSettingsFr().apply {
                findNavController().navigate(this)
            }
        }

        binding.exitButton.setOnClickListener {
            MainFragmentDirections.actionLogout().apply {
                findNavController().navigate(this)
            }
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.RECORD_AUDIO) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),0
            )}
    }

    private fun loadUsername() : String? {
        val sharedPreferences = activity?.getSharedPreferences(Variables.sharedPrefLogin, Context.MODE_PRIVATE)!!
        return sharedPreferences.getString(Variables.username, null)
    }
}