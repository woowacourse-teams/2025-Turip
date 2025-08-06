package com.on.turip.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.on.turip.domain.region.Region
import com.on.turip.domain.region.RegionType
import com.on.turip.ui.common.model.RegionModel

class HomeViewModel : ViewModel() {
    val metropolitanCities: LiveData<List<RegionModel>> =
        liveData {
            val metropolitans: List<Region> = Region.from(RegionType.METROPOLITAN_CITY)
            emit(metropolitans.map(RegionModel::find))
        } // TODO: 추후 수정 예정
}
