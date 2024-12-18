package de.dimedis.mobileentry.fragments;

import static de.dimedis.mobileentry.util.ConfigPrefHelper.getBorders;
import static de.dimedis.mobileentry.util.ConfigPrefHelper.setUsersBorder;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import de.dimedis.mobileentry.adapter.ItemLocationAdapter;
import de.dimedis.mobileentry.backend.response.Border;
import de.dimedis.mobileentry.databinding.FragmentLocationBinding;
import de.dimedis.mobileentry.ui.ScanActivity;

public class LocationFragment extends BaseFragment {
    static final String TAG = "LanguageFragment";

    FragmentLocationBinding binding;

    public LocationFragment() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        public Builder() {
        }

        public LocationFragment build() {
            return new LocationFragment();
        }
    }

    List<ItemLocationAdapter.Data> mapLanguage;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FragmentLocationBinding.inflate(inflater, container, false);

        ItemLocationAdapter adapter = new ItemLocationAdapter(getLanguages());
        adapter.setOnItemClick((holder, data, position) -> {
            setUsersBorder(data.code);
            ScanActivity.scanEntry(getContext());
            getActivity().finish();
        });
        binding.location.setHasFixedSize(true);
        binding.location.setAdapter(adapter);
        binding.location.setLayoutManager(new LinearLayoutManager(getActivity()));

        return binding.getRoot();
    }

    List<ItemLocationAdapter.Data> getLanguages() {
        mapLanguage = new ArrayList<>();
        List<Border> borders = getBorders();
        if (null != borders) {
            for (Border entry : borders) {
                Log.i(TAG, "LANG " + entry.getBorderName() + "|" + entry.getFairName());
                mapLanguage.add(new ItemLocationAdapter.Data(entry));
            }
        }
        return mapLanguage;
    }
}
