package com.ebpearls.sample.generated.graphql.domain.repos.query

import kotlinx.coroutines.flow.Flow
import type.GenderListAdminBody
import type.GetSinglePage
import type.GetSinglePageBYID
import type.InterestListAdminBody
import type.IntroDeleteBody
import type.IntroNotificationBody
import type.ListEmailTemplatesBody
import type.LookingForGenderListAdminBody
import type.PaginationBody
import type.PaginationWithUserIdBody
import type.UserDetailsBody
import type.UserListBody

public interface Repository {
  public fun getMyIntros(body: PaginationBody?): Flow<GetMyIntrosQuery.Data>

  public fun getOtherUsersIntros(body: PaginationWithUserIdBody?):
      Flow<GetOtherUsersIntrosQuery.Data>

  public fun getOtherUserActiveIntro(body: IntroNotificationBody?):
      Flow<GetOtherUserActiveIntroQuery.Data>

  public fun getLikedIntros(body: PaginationBody?): Flow<GetLikedIntrosQuery.Data>

  public fun getPendingIntros(body: PaginationBody?): Flow<GetPendingIntrosQuery.Data>

  public fun getOtherUsersIntro(body: PaginationWithUserIdBody?): Flow<GetOtherUsersIntroQuery.Data>

  public fun introDetails(body: IntroDeleteBody?): Flow<IntroDetailsQuery.Data>

  public fun introFeed(body: PaginationBody?): Flow<IntroFeedQuery.Data>

  public fun getReportedIntro(body: PaginationBody?): Flow<GetReportedIntroQuery.Data>

  public fun getMusic(body: PaginationBody?): Flow<GetMusicQuery.Data>

  public fun Notification(body: PaginationBody?): Flow<NotificationQuery.Data>

  public fun NotificationSettingsDetails(): Flow<NotificationSettingsDetailsQuery.Data>

  public fun subscriptionDetails(): Flow<SubscriptionDetailsQuery.Data>

  public fun userSetting(): Flow<UserSettingQuery.Data>

  public fun authenticatedUser(): Flow<AuthenticatedUserQuery.Data>

  public fun getAdmins(body: UserListBody?): Flow<GetAdminsQuery.Data>

  public fun getAdminDetails(body: UserDetailsBody?): Flow<GetAdminDetailsQuery.Data>

  public fun getUsers(body: UserListBody?): Flow<GetUsersQuery.Data>

  public fun getTotalUser(): Flow<GetTotalUserQuery.Data>

  public fun getUserDetails(body: UserDetailsBody?): Flow<GetUserDetailsQuery.Data>

  public fun getUserWithGender(): Flow<GetUserWithGenderQuery.Data>

  public fun getMatchRates(): Flow<GetMatchRatesQuery.Data>

  public fun getGendersAdmin(body: GenderListAdminBody?): Flow<GetGendersAdminQuery.Data>

  public fun getLookingForGendersAdmin(body: LookingForGenderListAdminBody?):
      Flow<GetLookingForGendersAdminQuery.Data>

  public fun getLookingForGendersList(): Flow<GetLookingForGendersListQuery.Data>

  public fun getInterestAdmin(body: InterestListAdminBody?): Flow<GetInterestAdminQuery.Data>

  public fun getInterestUserCount(): Flow<GetInterestUserCountQuery.Data>

  public fun notificationTemplate(body: UserListBody?): Flow<NotificationTemplateQuery.Data>

  public fun getNotificationTemplateDetails(body: UserDetailsBody?):
      Flow<GetNotificationTemplateDetailsQuery.Data>

  public fun getNotificationList(body: UserListBody?): Flow<GetNotificationListQuery.Data>

  public fun getAdminNotification(body: UserListBody?): Flow<GetAdminNotificationQuery.Data>

  public fun getReportedUserForAdmin(body: GenderListAdminBody?):
      Flow<GetReportedUserForAdminQuery.Data>

  public fun getReportedUserCounts(body: GenderListAdminBody?):
      Flow<GetReportedUserCountsQuery.Data>

  public fun getReportedIntroForAdmin(body: GenderListAdminBody?):
      Flow<GetReportedIntroForAdminQuery.Data>

  public fun getIntroForAdmin(body: GenderListAdminBody?): Flow<GetIntroForAdminQuery.Data>

  public fun getAllPage(): Flow<GetAllPageQuery.Data>

  public fun getPage(body: GetSinglePage?): Flow<GetPageQuery.Data>

  public fun getPageByID(body: GetSinglePageBYID?): Flow<GetPageByIDQuery.Data>

  public fun listEmailTemplate(body: ListEmailTemplatesBody?): Flow<ListEmailTemplateQuery.Data>
}
