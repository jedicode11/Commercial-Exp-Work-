package de.dimedis.mobileentry.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.dimedis.mobileentry.adapter.ItemAdapter;
import de.dimedis.mobileentry.databinding.FragmentLanguageBinding;

public class LanguageFragment extends BaseFragment {
    static final String TAG = "LanguageFragment";

    FragmentLanguageBinding binding;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        public Builder() {
        }

        public LanguageFragment build() {
            return new LanguageFragment();
        }
    }

    List<ItemAdapter.Data> mapLanguage;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FragmentLanguageBinding.inflate(inflater, container, false);
        ItemAdapter adapter = new ItemAdapter(getLanguages());
        adapter.setOnItemClick((holder, data, position) -> {
            getConfigPreferences().setCurrentLanguage(data.code);
            fragment(DeviceInitFragment.builder().build());
        });
        binding.language.setHasFixedSize(true);
        binding.language.setAdapter(adapter);
        binding.language.setLayoutManager(new LinearLayoutManager(getActivity()));

        return binding.getRoot();
    }


    List<ItemAdapter.Data> getLanguages() {
        mapLanguage = new ArrayList<>();
        Gson gson = new Gson();
        String container = getConfigPreferences().languagesContainer();
        Map<String, String> mapLang = gson.fromJson(container, new TypeToken<Map<String, String>>() {
        }.getType());
        for (Map.Entry<String, String> entry : mapLang.entrySet()) {
            Log.i(TAG, "LANG " + entry.getKey() + "|" + entry.getValue());
            mapLanguage.add(new ItemAdapter.Data(entry.getKey(), entry.getValue()));
        }
        return mapLanguage;
    }
}
