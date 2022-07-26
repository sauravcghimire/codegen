package com.ebpearls.sample.generated.graphql.domain.repos.mutation

import kotlinx.coroutines.flow.Flow
import type.AdminAddGenderBody
import type.AdminAddInterestBody
import type.AdminAddLookingForGenderBody
import type.AdminChangePasswordBody
import type.AdminForgotPasswordBody
import type.AdminLoginBody
import type.AdminResetPasswordBody
import type.AdminUpdateGenderBody
import type.AdminUpdateInterestBody
import type.AdminUpdateLookingForGenderBody
import type.AdminUpdateUserBody
import type.AndroidInappPurchaseBody
import type.BadgeResetBody
import type.ChatNotificationBody
import type.ConsumeRewindQuotaInput
import type.CreatePageInput
import type.DeactivateUserBody
import type.IntroAppealUnblockBody
import type.IntroBody
import type.IntroDeleteBody
import type.IntroReportBody
import type.IntroUpdateBody
import type.IosInappPurchaseBody
import type.LikeDislikeInput
import type.NotificationSettingInput
import type.NotificationTemplateUpdateBody
import type.PageUpdateBody
import type.RefreshTokenBody
import type.UnfollowedUser
import type.UpdateEmailTemplateBody
import type.UserDetailsBody
import type.UserUpdateBody
import type.UserVerificationBody

public interface Repository {
  public fun addIntro(body: IntroBody?): Flow<AddIntroMutation.Data>

  public fun updateIntro(body: IntroUpdateBody?): Flow<UpdateIntroMutation.Data>

  public fun deleteIntro(body: IntroDeleteBody?): Flow<DeleteIntroMutation.Data>

  public fun appealForIntroUnblock(body: IntroAppealUnblockBody?):
      Flow<AppealForIntroUnblockMutation.Data>

  public fun likeDislike(body: LikeDislikeInput?): Flow<LikeDislikeMutation.Data>

  public fun consumeRewindQuota(body: ConsumeRewindQuotaInput?):
      Flow<ConsumeRewindQuotaMutation.Data>

  public fun reportIntro(body: IntroReportBody?): Flow<ReportIntroMutation.Data>

  public fun notificationSettings(body: NotificationSettingInput?):
      Flow<NotificationSettingsMutation.Data>

  public fun readNotification(body: IntroDeleteBody?): Flow<ReadNotificationMutation.Data>

  public fun readAllNotification(): Flow<ReadAllNotificationMutation.Data>

  public fun clearNotificationBadge(body: BadgeResetBody?):
      Flow<ClearNotificationBadgeMutation.Data>

  public fun sendChatNotification(body: ChatNotificationBody?):
      Flow<SendChatNotificationMutation.Data>

  public fun verifyIosSubscriptionToken(body: IosInappPurchaseBody?):
      Flow<VerifyIosSubscriptionTokenMutation.Data>

  public fun verifyAndroidSubscriptionToken(body: AndroidInappPurchaseBody?):
      Flow<VerifyAndroidSubscriptionTokenMutation.Data>

  public fun hardPass(body: UnfollowedUser?): Flow<HardPassMutation.Data>

  public fun adminLogin(body: AdminLoginBody?): Flow<AdminLoginMutation.Data>

  public fun forgetPassword(body: AdminForgotPasswordBody?): Flow<ForgetPasswordMutation.Data>

  public fun resetPassword(body: AdminResetPasswordBody?): Flow<ResetPasswordMutation.Data>

  public fun refreshToken(body: RefreshTokenBody?): Flow<RefreshTokenMutation.Data>

  public fun changePassword(body: AdminChangePasswordBody?): Flow<ChangePasswordMutation.Data>

  public fun updateAdmin(body: AdminUpdateUserBody?): Flow<UpdateAdminMutation.Data>

  public fun deleteAdmin(body: UserDetailsBody?): Flow<DeleteAdminMutation.Data>

  public fun updateUser(body: UserUpdateBody?): Flow<UpdateUserMutation.Data>

  public fun deleteUser(body: UserDetailsBody?): Flow<DeleteUserMutation.Data>

  public fun deactivateUser(body: DeactivateUserBody?): Flow<DeactivateUserMutation.Data>

  public fun verifyUser(body: UserVerificationBody?): Flow<VerifyUserMutation.Data>

  public fun addGender(body: AdminAddGenderBody?): Flow<AddGenderMutation.Data>

  public fun updateGender(body: AdminUpdateGenderBody?): Flow<UpdateGenderMutation.Data>

  public fun deleteGender(body: UserDetailsBody?): Flow<DeleteGenderMutation.Data>

  public fun addLookingForGender(body: AdminAddLookingForGenderBody?):
      Flow<AddLookingForGenderMutation.Data>

  public fun updateLookingForGender(body: AdminUpdateLookingForGenderBody?):
      Flow<UpdateLookingForGenderMutation.Data>

  public fun deleteLookingForGender(body: UserDetailsBody?):
      Flow<DeleteLookingForGenderMutation.Data>

  public fun addInterest(body: AdminAddInterestBody?): Flow<AddInterestMutation.Data>

  public fun updateInterest(body: AdminUpdateInterestBody?): Flow<UpdateInterestMutation.Data>

  public fun deleteInterest(body: UserDetailsBody?): Flow<DeleteInterestMutation.Data>

  public fun updateNotificationTemplate(body: NotificationTemplateUpdateBody?):
      Flow<UpdateNotificationTemplateMutation.Data>

  public fun resetAdminNotificationCount(): Flow<ResetAdminNotificationCountMutation.Data>

  public fun deleteIntroFromAdmin(body: IntroDeleteBody?): Flow<DeleteIntroFromAdminMutation.Data>

  public fun activeInactiveIntro(body: IntroDeleteBody?): Flow<ActiveInactiveIntroMutation.Data>

  public fun addPage(createPageInput: CreatePageInput?): Flow<AddPageMutation.Data>

  public fun updatePage(body: PageUpdateBody?): Flow<UpdatePageMutation.Data>

  public fun updateEmailTemplate(body: UpdateEmailTemplateBody?):
      Flow<UpdateEmailTemplateMutation.Data>
}
