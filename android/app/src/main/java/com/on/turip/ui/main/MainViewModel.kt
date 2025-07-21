package com.on.turip.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.on.turip.domain.region.Region
import com.on.turip.domain.region.RegionType
import com.on.turip.ui.common.model.RegionModel

class MainViewModel : ViewModel() {
    val metropolitanCities: LiveData<List<RegionModel>> =
        liveData {
            val metropolitans: List<Region> = Region.from(RegionType.METROPOLITAN_CITY)
            emit(metropolitans.map(RegionModel::find))
        }

    val provinces: LiveData<List<RegionModel>> =
        liveData {
            val provinces: List<Region> = Region.from(RegionType.PROVINCE)
            emit(provinces.map(RegionModel::find))
        }
}
