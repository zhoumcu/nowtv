package com.pccw.nowplayer.model;

import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.pccw.nowplayer.model.exceptions.LibraryException;
import com.pccw.nowplayer.utils.LString;
import com.pccw.nowplayer.utils.PromiseUtils;
import com.pccw.nowtv.nmaf.checkout.BasicCheckoutModels;
import com.pccw.nowtv.nmaf.checkout.NMAFBasicCheckout;
import com.pccw.nowtv.nmaf.npx.mynow.DataModels;
import com.pccw.nowtv.nmaf.npx.mynow.NPXMyNow;

import org.jdeferred.Deferred;
import org.jdeferred.DonePipe;
import org.jdeferred.Promise;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by kriz on 11/8/2016.
 */
public class BPLClient {
    private static BPLClient instance;

    private BPLClient() {
    }

    public static BPLClient getInstance() {
        if (instance == null) {
            synchronized (BPLClient.class) {
                if (instance == null) instance = new BPLClient();
            }
        }
        return instance;
    }

    private static LString getName(DataModels.NPXMyNowMatchAlertGetTeamListTeamModel team) {
        if (team == null || team.team_name == null || team.team_name.lang == null || team.team_name.lang.length == 0) return null;

        String eng = null;
        String chi = null;

        for (DataModels.NPXMyNowMatchAlertGetTeamListLangModel lang : team.team_name.lang) {
            if (lang == null || lang.code == null) continue;
            if (lang.code.contains("en")) {
                eng = lang.short_name;
            } else if (lang.code.contains("zh")) {
                chi = lang.short_name;
            }
        }
        return LString.make(eng, chi);
    }

    public Promise<Boolean, Throwable, Void> getAlertEnabled() {
        return PromiseUtils.promise(new PromiseUtils.Task<Boolean>() {
            @Override
            public void run(final Deferred<Boolean, Throwable, Void> deferred) {
                NPXMyNow.getSharedInstance().matchAlertGetNotification(new NPXMyNow.NPXMyNowCallback<DataModels.NPXMyNowMatchAlertGetNotificationOutputModel>() {
                    @Override
                    public void onRequestFailed(int i) {
                        deferred.reject(new LibraryException(i));
                    }

                    @Override
                    public void onRequestSuccessful(DataModels.NPXMyNowMatchAlertGetNotificationOutputModel response) {
                        deferred.resolve(isAlertEnabled(response));
                    }
                });
            }
        });
    }

    private boolean isAlertEnabled(DataModels.NPXMyNowMatchAlertGetNotificationOutputModel response) {
        if (response != null && response.response != null) for (DataModels.NPXMyNowMatchAlertGetNotificationResponseModel res : response.response) {
            if (res == null || res.platform == null) continue;
            if (res.platform.equals("15")) return res.enabled != 0;
        }
        return false;
    }

    public Promise<BasicCheckoutModels.NMAFGetDeviceControlStatusOutputModel, Throwable, Void> loadDeviceControlStatus() {
        return PromiseUtils.promise(new PromiseUtils.Task<BasicCheckoutModels.NMAFGetDeviceControlStatusOutputModel>() {
            @Override
            public void run(final Deferred<BasicCheckoutModels.NMAFGetDeviceControlStatusOutputModel, Throwable, Void> deferred) {
                NMAFBasicCheckout.getSharedInstance().getDeviceControlStatus(new NMAFBasicCheckout.GetDeviceControlStatusCallback() {
                    @Override
                    public void onGetDeviceControlStatusFailed(@NonNull Throwable throwable) {
                        deferred.reject(throwable);
                    }

                    @Override
                    public void onGetDeviceControlStatusSuccess(@NonNull BasicCheckoutModels.NMAFGetDeviceControlStatusOutputModel response) {
                        deferred.resolve(response);
                    }
                });
            }
        });
    }

    public Promise<List<Team>, Throwable, Void> loadFavoriteTeams() {

        return PromiseUtils.promise(new PromiseUtils.Task<List<Team>>() {
            @Override
            public void run(final Deferred<List<Team>, Throwable, Void> deferred) {
                NPXMyNow.getSharedInstance().matchAlertGetFavoriteTeam(new NPXMyNow.NPXMyNowCallback<DataModels.NPXMyNowMatchAlertGetFavoriteTeamOutputModel>() {
                    @Override
                    public void onRequestFailed(int i) {
                        deferred.reject(new LibraryException(i));
                    }

                    @Override
                    public void onRequestSuccessful(DataModels.NPXMyNowMatchAlertGetFavoriteTeamOutputModel response) {
                        // filter out disabled teams
                        List<Team> teams = new ArrayList<>();
                        if (response != null && response.response != null) {
                            for (DataModels.NPXMyNowMatchAlertGetFavoriteTeamResponseModel team : response.response) {
                                if (team.enabled != 0) teams.add(new Team(team));
                            }
                        }
                        deferred.resolve(teams);
                    }
                });
            }
        });
    }

    public Promise<List<Team>, Throwable, Void> loadTeams() {

        return PromiseUtils.promise(new PromiseUtils.Task<List<Team>>() {
            @Override
            public void run(final Deferred<List<Team>, Throwable, Void> deferred) {

                NPXMyNow.getSharedInstance().matchAlertGetTeamList(new NPXMyNow.NPXMyNowCallback<DataModels.NPXMyNowMatchAlertGetTeamListDataModel>() {
                    @Override
                    public void onRequestFailed(int i) {
                        deferred.reject(new LibraryException(i));
                    }

                    @Override
                    public void onRequestSuccessful(DataModels.NPXMyNowMatchAlertGetTeamListDataModel response) {

                        // filter out disabled teams
                        List<Team> teams = new ArrayList<>();
                        if (response != null && response.teams != null && response.teams.team != null) {
                            for (DataModels.NPXMyNowMatchAlertGetTeamListTeamModel team : response.teams.team) {
                                if (team.is_enabled != 0) teams.add(new Team(team));
                            }
                        }

                        // sort team using English short name
                        Collections.sort(teams, new Comparator<Team>() {
                            @Override
                            public int compare(Team lhs, Team rhs) {
                                if (lhs == rhs) return 0;
                                if (lhs == null) return -1;
                                if (rhs == null) return 1;

                                String lhsStr = lhs.sortKey;
                                String rhsStr = rhs.sortKey;
                                if (lhsStr == rhsStr) return 0;
                                if (lhsStr == null) return -1;
                                if (rhsStr == null) return 1;

                                return lhsStr.compareTo(rhsStr);
                            }
                        });

                        deferred.resolve(teams);
                    }
                });
            }
        });
    }

    public Promise<Void, Throwable, Void> saveFavoriteTeams(final List<Team> newTeams) {

        return PromiseUtils.firstly(new DonePipe<Void, List<Team>, Throwable, Void>() {
            @Override
            public Promise<List<Team>, Throwable, Void> pipeDone(Void result) {
                return loadFavoriteTeams();
            }
        }).then(new DonePipe<List<Team>, Void, Throwable, Void>() {
            @Override
            public Promise<Void, Throwable, Void> pipeDone(List<Team> currentTeams) {
                return saveFavoriteTeams(newTeams, currentTeams);
            }
        });
    }

    private Promise<Void, Throwable, Void> saveFavoriteTeams(List<Team> newTeams, List<Team> currentTeams) {

        SparseArray<Team> currentTeamMap = new SparseArray<>();
        if (currentTeams != null) for (Team t : currentTeams) {
            currentTeamMap.put(t.id, t);
        }

        SparseArray<Team> newTeamMap = new SparseArray<>();
        if (newTeams != null) for (Team t : newTeams) {
            newTeamMap.put(t.id, t);
        }

        // find deleted teams: teams in currentTeams but not in newTeams
        List<DataModels.NPXMyNowMatchAlertGetFavoriteTeamResponseModel> deleted = new ArrayList<>();
        if (currentTeams != null) for (Team team : currentTeams) {
            Team t = newTeamMap.get(team.id);
            if (t == null) {
                deleted.add(new DataModels.NPXMyNowMatchAlertGetFavoriteTeamResponseModel(team.id, false));
            }
        }

        // find added teams: teams in newTeams but not in currentTeams
        List<DataModels.NPXMyNowMatchAlertGetFavoriteTeamResponseModel> added = new ArrayList<>();
        if (newTeams != null) for (Team team : newTeams) {
            Team t = currentTeamMap.get(team.id);
            if (t == null) {
                added.add(new DataModels.NPXMyNowMatchAlertGetFavoriteTeamResponseModel(team.id, true));
            }
        }

        final List<DataModels.NPXMyNowMatchAlertGetFavoriteTeamResponseModel> changed = new ArrayList<>();
        changed.addAll(deleted);
        changed.addAll(added);

        return PromiseUtils.promise(new PromiseUtils.Task<Void>() {
            @Override
            public void run(final Deferred<Void, Throwable, Void> deferred) {
                if (changed.isEmpty()) {
                    deferred.resolve(null);
                } else {
                    NPXMyNow.getSharedInstance().matchAlertSetFavoriteTeam(changed, new NPXMyNow.NPXMyNowCallback<DataModels.NPXMyNowMatchAlertGetFavoriteTeamOutputModel>() {
                        @Override
                        public void onRequestFailed(int i) {
                            deferred.reject(new LibraryException(i));
                        }

                        @Override
                        public void onRequestSuccessful(DataModels.NPXMyNowMatchAlertGetFavoriteTeamOutputModel npxMyNowMatchAlertGetFavoriteTeamOutputModel) {
                            deferred.resolve(null);
                        }
                    });
                }
            }
        });
    }

    public Promise<Boolean, Throwable, Void> setAlertEnabled(final boolean enabled) {
        return PromiseUtils.promise(new PromiseUtils.Task<Void>() {
            @Override
            public void run(final Deferred<Void, Throwable, Void> deferred) {
                NPXMyNow.getSharedInstance().matchAlertSetNotification("15", enabled, new NPXMyNow.NPXMyNowCallback<DataModels.NPXMyNowMatchAlertGetNotificationOutputModel>() {
                    @Override
                    public void onRequestFailed(int i) {
                        deferred.reject(new LibraryException(i));
                    }

                    @Override
                    public void onRequestSuccessful(DataModels.NPXMyNowMatchAlertGetNotificationOutputModel npxMyNowMatchAlertGetNotificationOutputModel) {
                        deferred.resolve(null);
                    }
                });
            }
        }).then(new DonePipe<Void, Boolean, Throwable, Void>() {
            @Override
            public Promise<Boolean, Throwable, Void> pipeDone(Void result) {
                return getAlertEnabled();
            }
        });
    }

    public static class Team {

        public int id;
        public String sortKey;
        public LString teamName;

        public Team(DataModels.NPXMyNowMatchAlertGetTeamListTeamModel t) {
            this.id = t.id;
            this.teamName = BPLClient.getName(t);
            this.sortKey = this.teamName.getEnglishString();
        }

        public Team(DataModels.NPXMyNowMatchAlertGetFavoriteTeamResponseModel t) {
            this.id = t.teamId;
        }
    }
}
