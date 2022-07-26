package com.ebpearls.sample.generated.graphql.`data`.repos.query

import AuthenticatedUserQuery
import GetAdminDetailsQuery
import GetAdminNotificationQuery
import GetAdminsQuery
import GetAllPageQuery
import GetGendersAdminQuery
import GetInterestAdminQuery
import GetInterestUserCountQuery
import GetIntroForAdminQuery
import GetLikedIntrosQuery
import GetLookingForGendersAdminQuery
import GetLookingForGendersListQuery
import GetMatchRatesQuery
import GetMusicQuery
import GetMyIntrosQuery
import GetNotificationListQuery
import GetNotificationTemplateDetailsQuery
import GetOtherUserActiveIntroQuery
import GetOtherUsersIntroQuery
import GetOtherUsersIntrosQuery
import GetPageByIDQuery
import GetPageQuery
import GetPendingIntrosQuery
import GetReportedIntroForAdminQuery
import GetReportedIntroQuery
import GetReportedUserCountsQuery
import GetReportedUserForAdminQuery
import GetTotalUserQuery
import GetUserDetailsQuery
import GetUserWithGenderQuery
import GetUsersQuery
import IntroDetailsQuery
import IntroFeedQuery
import ListEmailTemplateQuery
import NotificationQuery
import NotificationSettingsDetailsQuery
import NotificationTemplateQuery
import SubscriptionDetailsQuery
import UserSettingQuery
import com.ebpearls.sample.generated.graphql.domain.repos.query.Repository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import type.*

public class RepositoryImpl(
    public val ioDispatcher: CoroutineDispatcher,
) : Repository {
    public override fun getMyIntros(body: PaginationBody?): Flow<GetMyIntrosQuery.Data> = flow {
    }.flowOn(ioDispatcher)

    public override fun getOtherUsersIntros(body: PaginationWithUserIdBody?):
            Flow<GetOtherUsersIntrosQuery.Data> = flow {
    }.flowOn(ioDispatcher)

    public override fun getOtherUserActiveIntro(body: IntroNotificationBody?):
            Flow<GetOtherUserActiveIntroQuery.Data> = flow {
    }.flowOn(ioDispatcher)

    public override fun getLikedIntros(body: PaginationBody?): Flow<GetLikedIntrosQuery.Data> =
        flow {
        }.flowOn(ioDispatcher)

    public override fun getPendingIntros(body: PaginationBody?): Flow<GetPendingIntrosQuery.Data> =
        flow {
        }.flowOn(ioDispatcher)

    public override fun getOtherUsersIntro(body: PaginationWithUserIdBody?):
            Flow<GetOtherUsersIntroQuery.Data> = flow {
    }.flowOn(ioDispatcher)

    public override fun introDetails(body: IntroDeleteBody?): Flow<IntroDetailsQuery.Data> = flow {
    }.flowOn(ioDispatcher)

    public override fun introFeed(body: PaginationBody?): Flow<IntroFeedQuery.Data> = flow {
    }.flowOn(ioDispatcher)

    public override fun getReportedIntro(body: PaginationBody?): Flow<GetReportedIntroQuery.Data> =
        flow {
        }.flowOn(ioDispatcher)

    public override fun getMusic(body: PaginationBody?): Flow<GetMusicQuery.Data> = flow {
    }.flowOn(ioDispatcher)

    public override fun Notification(body: PaginationBody?): Flow<NotificationQuery.Data> = flow {
    }.flowOn(ioDispatcher)

    public override fun NotificationSettingsDetails(): Flow<NotificationSettingsDetailsQuery.Data> =
        flow {
        }.flowOn(ioDispatcher)

    public override fun subscriptionDetails(): Flow<SubscriptionDetailsQuery.Data> = flow {
    }.flowOn(ioDispatcher)

    public override fun userSetting(): Flow<UserSettingQuery.Data> = flow {
    }.flowOn(ioDispatcher)

    public override fun authenticatedUser(): Flow<AuthenticatedUserQuery.Data> = flow {
    }.flowOn(ioDispatcher)

    public override fun getAdmins(body: UserListBody?): Flow<GetAdminsQuery.Data> = flow {
    }.flowOn(ioDispatcher)

    public override fun getAdminDetails(body: UserDetailsBody?): Flow<GetAdminDetailsQuery.Data> =
        flow {
        }.flowOn(ioDispatcher)

    public override fun getUsers(body: UserListBody?): Flow<GetUsersQuery.Data> = flow {
    }.flowOn(ioDispatcher)

    public override fun getTotalUser(): Flow<GetTotalUserQuery.Data> = flow {
    }.flowOn(ioDispatcher)

    public override fun getUserDetails(body: UserDetailsBody?): Flow<GetUserDetailsQuery.Data> =
        flow {
        }.flowOn(ioDispatcher)

    public override fun getUserWithGender(): Flow<GetUserWithGenderQuery.Data> = flow {
    }.flowOn(ioDispatcher)

    public override fun getMatchRates(): Flow<GetMatchRatesQuery.Data> = flow {
    }.flowOn(ioDispatcher)

    public override fun getGendersAdmin(body: GenderListAdminBody?): Flow<GetGendersAdminQuery.Data> =
        flow {
        }.flowOn(ioDispatcher)

    public override fun getLookingForGendersAdmin(body: LookingForGenderListAdminBody?):
            Flow<GetLookingForGendersAdminQuery.Data> = flow {
    }.flowOn(ioDispatcher)

    public override fun getLookingForGendersList(): Flow<GetLookingForGendersListQuery.Data> =
        flow {
        }.flowOn(ioDispatcher)

    public override fun getInterestAdmin(body: InterestListAdminBody?):
            Flow<GetInterestAdminQuery.Data> = flow {
    }.flowOn(ioDispatcher)

    public override fun getInterestUserCount(): Flow<GetInterestUserCountQuery.Data> = flow {
    }.flowOn(ioDispatcher)

    public override fun notificationTemplate(body: UserListBody?):
            Flow<NotificationTemplateQuery.Data> = flow {
    }.flowOn(ioDispatcher)

    public override fun getNotificationTemplateDetails(body: UserDetailsBody?):
            Flow<GetNotificationTemplateDetailsQuery.Data> = flow {
    }.flowOn(ioDispatcher)

    public override fun getNotificationList(body: UserListBody?): Flow<GetNotificationListQuery.Data> =
        flow {
        }.flowOn(ioDispatcher)

    public override fun getAdminNotification(body: UserListBody?):
            Flow<GetAdminNotificationQuery.Data> = flow {
    }.flowOn(ioDispatcher)

    public override fun getReportedUserForAdmin(body: GenderListAdminBody?):
            Flow<GetReportedUserForAdminQuery.Data> = flow {
    }.flowOn(ioDispatcher)

    public override fun getReportedUserCounts(body: GenderListAdminBody?):
            Flow<GetReportedUserCountsQuery.Data> = flow {
    }.flowOn(ioDispatcher)

    public override fun getReportedIntroForAdmin(body: GenderListAdminBody?):
            Flow<GetReportedIntroForAdminQuery.Data> = flow {
    }.flowOn(ioDispatcher)

    public override fun getIntroForAdmin(body: GenderListAdminBody?): Flow<GetIntroForAdminQuery.Data> =
        flow {
        }.flowOn(ioDispatcher)

    public override fun getAllPage(): Flow<GetAllPageQuery.Data> = flow {
    }.flowOn(ioDispatcher)

    public override fun getPage(body: GetSinglePage?): Flow<GetPageQuery.Data> = flow {
    }.flowOn(ioDispatcher)

    public override fun getPageByID(body: GetSinglePageBYID?): Flow<GetPageByIDQuery.Data> = flow {
    }.flowOn(ioDispatcher)

    public override fun listEmailTemplate(body: ListEmailTemplatesBody?):
            Flow<ListEmailTemplateQuery.Data> = flow {
    }.flowOn(ioDispatcher)
}
