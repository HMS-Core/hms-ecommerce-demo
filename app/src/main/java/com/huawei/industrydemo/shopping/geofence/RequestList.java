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

package com.huawei.industrydemo.shopping.geofence;

import android.app.PendingIntent;

import com.huawei.hms.location.Geofence;

import java.util.ArrayList;

import static com.huawei.industrydemo.shopping.constants.Constants.EMPTY;

public class RequestList {
    public PendingIntent intnet;

    public int requestCode;

    public ArrayList<Geofence> geofences;

    public RequestList(PendingIntent intnet, int requestCode, ArrayList<Geofence> geofences) {
        this.intnet = intnet;
        this.requestCode = requestCode;
        this.geofences = geofences;
    }

    public String show() {
        StringBuilder buf = new StringBuilder();
        String s = EMPTY;
        for (int i = 0; i < geofences.size(); i++) {
            buf.append("PendingIntent: ")
                .append(requestCode)
                .append(" UniqueID: ")
                .append(geofences.get(i).getUniqueId())
                .append("\n");
        }
        s = buf.toString();
        return s;
    }

    public boolean checkID() {
        ArrayList<Geofence> list = GeoFenceData.returnList();
        for (int j = 0; j < list.size(); j++) {
            String s = list.get(j).getUniqueId();
            for (int i = 0; i < geofences.size(); i++) {
                if (s.equals(geofences.get(i).getUniqueId())) {
                    // id already exist
                    return true;
                }
            }
        }
        return false;
    }

    public void removeID(String[] str) {
        for (String s : str) {
            for (int j = geofences.size() - 1; j >= 0; j--) {
                if (s.equals(geofences.get(j).getUniqueId())) {
                    geofences.remove(j);
                }
            }
        }
    }
}