import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ResourceEndpoints } from '../types/api-const';
import { Account, JoinLink, MessageOutput } from '../types/api-output';
import { AccountCreateRequest } from '../types/api-request';
import { HttpRequestService } from './http-request.service';

/**
 * Handles account related logic provision
 */
@Injectable({
  providedIn: 'root',
})
export class AccountService {
  constructor(private httpRequestService: HttpRequestService) {
  }

  /**
   * Creates an account by calling API.
   */
  createAccount(key: string, timezone: string): Observable<MessageOutput> {
    const paramMap: Record<string, string> = { key };
    if (timezone) {
      paramMap.timezone = timezone;
    }
    return this.httpRequestService.post(ResourceEndpoints.ACCOUNT, paramMap);
  }

  /**
   * Creates an account request by calling API.
   */
  createAccountRequest(request: AccountCreateRequest): Observable<JoinLink> {
    return this.httpRequestService.post(ResourceEndpoints.ACCOUNT_REQUEST, {}, request);
  }

  /**
   * Downgrades an account from instructor to student by calling API.
   */
  downgradeAccount(id: string): Observable<MessageOutput> {
    const paramMap: Record<string, string> = {
      instructorid: id,
    };
    return this.httpRequestService.put(ResourceEndpoints.ACCOUNT_DOWNGRADE, paramMap);
  }

  /**
   * Deletes an account by calling API.
   */
  deleteAccount(id: string): Observable<MessageOutput> {
    const paramMap: Record<string, string> = {
      instructorid: id,
    };
    return this.httpRequestService.delete(ResourceEndpoints.ACCOUNT, paramMap);
  }

  /**
   * Deletes an account request by calling API.
   */
  deleteAccountRequest(email: string, institute: string): Observable<MessageOutput> {
    const paramMap: Record<string, string> = {
      instructoremail: email,
      instructorinstitution: institute,
    };
    return this.httpRequestService.delete(ResourceEndpoints.ACCOUNT_REQUEST, paramMap);
  }

  /**
   * Resets a student account by calling API.
   */
  resetStudentAccount(courseId: string, studentEmail: string): Observable<MessageOutput> {
    const paramMap: Record<string, string> = {
      courseid: courseId,
      studentemail: studentEmail,
    };
    return this.httpRequestService.put(ResourceEndpoints.ACCOUNT_RESET, paramMap);
  }

  /**
   * Resets an instructor account by calling API.
   */
  resetInstructorAccount(courseId: string, instructorEmail: string): Observable<MessageOutput> {
    const paramMap: Record<string, string> = {
      courseid: courseId,
      instructoremail: instructorEmail,
    };
    return this.httpRequestService.put(ResourceEndpoints.ACCOUNT_RESET, paramMap);
  }

  /**
   * Gets an account by calling API.
   */
  getAccount(googleId: string): Observable<Account> {
    const paramMap: Record<string, string> = {
      instructorid: googleId,
    };
    return this.httpRequestService.get(ResourceEndpoints.ACCOUNT, paramMap);
  }

}
