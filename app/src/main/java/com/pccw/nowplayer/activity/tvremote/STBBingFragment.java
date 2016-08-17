package com.pccw.nowplayer.activity.tvremote;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.activity.IActionBar;
import com.pccw.nowplayer.activity.MainActivity;
import com.pccw.nowplayer.fragment.MainBaseFragment;
import com.pccw.nowplayer.helper.DialogHelper;
import com.pccw.nowplayer.helper.RecycleViewManagerFactory;
import com.pccw.nowplayer.model.Device;
import com.pccw.nowplayer.utils.ColorUtils;
import com.pccw.nowplayer.utils.DeviceManager;
import com.pccw.nowtv.nmaf.core.NMAFBaseModule;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by swifty on 29/5/2016.
 */
public class STBBingFragment extends MainBaseFragment implements IActionBar {

    @Bind(R.id.device_list)
    RecyclerView deviceList;

    private BottomSheetDialog mBottomSheetDialog;

    protected void bindEvents() {
        refreshList();
    }

    private void changeTitle(String name) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setSubTitle(name);
        }
    }

    @Override
    public View createViews(LayoutInflater inflater, ViewGroup parentContainer) {
        View root = inflater.inflate(R.layout.fragment_stb_binding, parentContainer, false);
        ButterKnife.bind(this, root);
        deviceList.setLayoutManager(RecycleViewManagerFactory.verticalList(getContext()));
        bindEvents();
        return root;
    }

    private View getEditButton() {
        TextView textView = new TextView(getContext());
        textView.setText(getString(R.string.edit));


        textView.setTextColor(ColorUtils.getColorStatusList(getResources().getColor(R.color.now_grey), getResources().getColor(R.color.white)));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 29/5/2016 edit button
            }
        });
        return textView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void refreshList() {
        deviceList.setAdapter(new DeviceListAdapter(DeviceManager.getInstance().getAllDevices()));
    }

    @Override
    public ActionBar showActionBar() {
        String subTitle = null;
        if (!DeviceManager.getInstance().hasConnectDevice()) {
            subTitle = getString(R.string.select_device);
        } else {
            subTitle = DeviceManager.getInstance().getConnectDevice().name;
        }
        return new ActionBar(false, getString(R.string.tv_remote), false, subTitle, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, false, null);
    }

    private void showBottomSheetDialog(final Device device, boolean isConnected) {
        mBottomSheetDialog = new BottomSheetDialog(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_bottom_sheet, null);
        if (isConnected) {
            view.findViewById(R.id.connect_device).setVisibility(View.GONE);
            view.findViewById(R.id.disconnect_device).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBottomSheetDialog.dismiss();
                    DeviceManager.getInstance().unBindDevice(device, new NMAFBaseModule.ErrorCallback() {
                        @Override
                        public void operationComplete(Throwable throwable) {
                            if (!isAdded()) return;
                            if (throwable == null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        bindEvents();
                                    }
                                });
                            }
                        }
                    });
                }
            });
        } else {
            view.findViewById(R.id.disconnect_device).setVisibility(View.GONE);
            view.findViewById(R.id.connect_device).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBottomSheetDialog.dismiss();
                    DeviceManager.getInstance().bindDevice(device, new NMAFBaseModule.ErrorCallback() {
                        @Override
                        public void operationComplete(Throwable throwable) {
                            if (!isAdded()) return;
                            if (throwable == null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        bindEvents();
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }
        view.findViewById(R.id.rename_device).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                DialogHelper.createInputDialog(getContext(), getString(R.string.please_input_device_name), device.name, new DialogHelper.inputDialogCallBack() {
                    @Override
                    public void inputTextCancel(DialogInterface dialog) {
                    }

                    @Override
                    public void inputTextConfirm(DialogInterface dialog, String inputText, int which) {
                        device.name = inputText;
                        DeviceManager.getInstance().saveDevice(device);
                        refreshList();
                        dialog.dismiss();
                    }
                });
            }
        });
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
            }
        });
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });
    }

    public class DeviceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private int ADD_DEVICE_ITEM = 1;
        private int DEVICE_ITEM = 0;
        private List<Device> devices;

        public DeviceListAdapter(List<Device> devices) {
            if (devices == null) {
                devices = new ArrayList<>();
            }
            this.devices = devices;
        }

        private void getAddDeviceView(RecyclerView.ViewHolder holder) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogHelper.createInputDialog(getContext(), getString(R.string.input_code), new DialogHelper.inputDialogCallBack() {
                        @Override
                        public void inputTextCancel(DialogInterface dialog) {
                        }

                        @Override
                        public void inputTextConfirm(final DialogInterface dialog, String inputText, int which) {
                            if (TextUtils.isEmpty(inputText)) {
                                Toast.makeText(getContext(), getString(R.string.please_input_code), Toast.LENGTH_SHORT).show();
                            } else {
                                DeviceManager.getInstance().addDevice(inputText, new NMAFBaseModule.ErrorCallback() {
                                    @Override
                                    public void operationComplete(Throwable throwable) {
                                        if (throwable == null) {
                                            STBBingFragment.this.getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    dialog.dismiss();
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }

        private void getDeviceView(DeviceViewHolder holder, final int position) {
            holder.title.setText(devices.get(position).name);
            if (DeviceManager.getInstance().isConnectDevice(devices.get(position))) {
                holder.status.setText(getString(R.string.connected));
                changeTitle(devices.get(position).name);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showBottomSheetDialog(devices.get(position), true);
                    }
                });
            } else {
                holder.status.setText(getString(R.string.ready_to_cast));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showBottomSheetDialog(devices.get(position), false);
                    }
                });
            }

        }

        @Override
        public int getItemCount() {
            return devices.size();
        }

        @Override
        public int getItemViewType(int position) {
            return DEVICE_ITEM;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof DeviceViewHolder) {
                getDeviceView((DeviceViewHolder) holder, position);
            } else {
                getAddDeviceView(holder);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == DEVICE_ITEM) {
                return new DeviceViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.view_stb_device_item, parent, false));
            } else {
                return new AddDeviceViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.view_add_stb_device_item, parent, false));
            }
        }

        class AddDeviceViewHolder extends RecyclerView.ViewHolder {
            public AddDeviceViewHolder(View itemView) {
                super(itemView);
            }
        }

        class DeviceViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.status)
            TextView status;
            @Bind(R.id.title)
            TextView title;

            public DeviceViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
