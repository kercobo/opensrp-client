package org.ei.opensrp.view.dialog;

import org.ei.opensrp.R;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;

import static org.ei.opensrp.Context.getInstance;

public class FPDMPAServiceMode extends FPAllMethodsServiceMode {

    public FPDMPAServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return getInstance().getStringResource(R.string.fp_register_service_mode_dmpa);
    }
}
