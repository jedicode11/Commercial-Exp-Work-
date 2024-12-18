package de.dimedis.mobileentry.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.dimedis.mobileentry.databinding.FragmentVoidticketBinding;

public class VoidTicketFragment extends BaseFragment {

    FragmentVoidticketBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FragmentVoidticketBinding.inflate(inflater, container, false);

        binding.ok.setOnClickListener(view -> back());
        binding.cancel.setOnClickListener(view -> back());
        return binding.getRoot();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        public Builder() {
        }

        public VoidTicketFragment build() {
            return new VoidTicketFragment();
        }
    }
}
