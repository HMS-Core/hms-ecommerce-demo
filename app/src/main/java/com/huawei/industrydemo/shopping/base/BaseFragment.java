/*
    Copyright 2020-2021. Huawei Technologies Co., Ltd. All rights reserved.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.huawei.industrydemo.shopping.base;

import androidx.fragment.app.Fragment;

import com.huawei.industrydemo.shopping.constants.KitConstants;
import com.huawei.industrydemo.shopping.constants.LogConfig;
import com.huawei.industrydemo.shopping.utils.KitTipUtil;

/**
 * Base Fragment
 *
 * @version [Ecommerce-Demo 1.0.0.300, 2020/9/16]
 * @since [Ecommerce-Demo 1.0.0.300]
 */
public class BaseFragment extends Fragment implements LogConfig, KitConstants {

    // Used Kit
    private String[] kits = null;

    public void addTipView() {
        KitTipUtil.addTipView(getActivity(), KitTipUtil.getKitMap(kits));
    }

    protected void setKits(String[] kits) {
        this.kits = kits;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && kits != null) { // show current fragment
            addTipView();
        }
    }
}
