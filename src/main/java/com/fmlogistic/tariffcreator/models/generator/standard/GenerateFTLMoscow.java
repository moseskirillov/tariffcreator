package com.fmlogistic.tariffcreator.models.generator.standard;

import com.fmlogistic.tariffcreator.models.generator.additional.AdditionalPointFTL;
import com.fmlogistic.tariffcreator.models.generator.additional.ForwardingFTL;
import com.fmlogistic.tariffcreator.models.generator.additional.LoadingUnloadingFTL;
import com.fmlogistic.tariffcreator.models.generator.additional.LoadingUnloadingFTLRows;
import com.fmlogistic.tariffcreator.models.generator.downtime.GenerateFTLDowntimeMSC;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GenerateFTLMoscow {
    private List<GenerateFTLMSCDeliveryTransport> mscDeliveryTCS;
    private List<GenerateFTLMSCDeliveryCapacity> mscDeliveryCapacity;
    private GenerateFTLDowntimeMSC downtimeMscRows;
    private List<AdditionalPointFTL> additionalPointFTLRows;
    private LoadingUnloadingFTL loadingUnloadingFTLRows;
    private ForwardingFTL forwardingFTLRows;
}
