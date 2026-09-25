package net.osslabz.electrum.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/** Status change of a subscribed script hash; the params are the script hash and its new status. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressNotification extends Notification<List<String>> {}
