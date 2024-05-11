package ru.nilsolk.gymapp.ui.calculators

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.nilsolk.gymapp.R
import ru.nilsolk.gymapp.databinding.FragmentCalculatorBinding

class CalculatorFragment : Fragment() {
    private lateinit var fragmentCalculatorBinding : FragmentCalculatorBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.visibility = View.GONE
        fragmentCalculatorBinding = FragmentCalculatorBinding.inflate(layoutInflater)
        return fragmentCalculatorBinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(fragmentCalculatorBinding)
        {

            bodyFatPercentageImage.setOnClickListener{
                val bodyFatAction =
                    ru.nilsolk.gymapp.fragment.CalculatorFragmentDirections.actionCalculatorFragmentToBodyFatPercentageFragment()
                Navigation.findNavController(requireView()).navigate(bodyFatAction)

            }
            calorieImage.setOnClickListener{
                val calorieAction =
                    ru.nilsolk.gymapp.fragment.CalculatorFragmentDirections.actionCalculatorFragmentToCalorieFragment()
                Navigation.findNavController(requireView()).navigate(calorieAction)
            }
            idealWeightImage.setOnClickListener{
                val idealWeightAction =
                    ru.nilsolk.gymapp.fragment.CalculatorFragmentDirections.actionCalculatorFragmentToIdealWeightFragment()
                Navigation.findNavController(requireView()).navigate(idealWeightAction)
            }
            jumpingRopeImage.setOnClickListener{
                val ropeCalorieAction =
                    ru.nilsolk.gymapp.fragment.CalculatorFragmentDirections.actionCalculatorFragmentToRopeCalorieBurnedFragment()
                Navigation.findNavController(requireView()).navigate(ropeCalorieAction)
            }
            oneRmImage.setOnClickListener{
                val oneRmAction =
                    ru.nilsolk.gymapp.fragment.CalculatorFragmentDirections.actionCalculatorFragmentToOneRmFragment()
                Navigation.findNavController(requireView()).navigate(oneRmAction)
            }
        }
    }
}