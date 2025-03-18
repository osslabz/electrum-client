package net.osslabz.electrum.result;

import com.fasterxml.jackson.annotation.JsonInclude;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressNotification extends Notification<String> {


}