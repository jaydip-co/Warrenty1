package com.jaydip.warrenty.Listeners;

import com.jaydip.warrenty.ItemActivity;
import com.jaydip.warrenty.Models.ItemModel;

public interface ActivityForResult {
    void satrtActivity(ItemModel model);
    void startshowActivity(ItemModel model);
    void startBillActivity(String Uri,boolean isBillPdf);
}
