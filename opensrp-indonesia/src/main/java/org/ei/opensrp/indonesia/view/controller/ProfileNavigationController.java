package org.ei.opensrp.indonesia.view.controller;

import android.content.Intent;

import org.ei.opensrp.indonesia.view.activity.CameraLaunchActivity;

import static org.ei.opensrp.AllConstants.CASE_ID;

public class ProfileNavigationController {

    public static void navigateToCamera(android.content.Context context, String caseId) {
        Intent intent = new Intent(context.getApplicationContext(), CameraLaunchActivity.class);
        intent.putExtra(CASE_ID, caseId);
        context.startActivity(intent);
    }


}
