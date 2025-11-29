import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

@Injectable({
    providedIn: 'root'
})
export class UserService {

    constructor(private apiService: ApiService) { }

    getUserPreferences(userId: number): Observable<any> {
        return this.apiService.get(`/users/${userId}/preferences`);
    }

    saveArticle(userId: number, articleData: any): Observable<any> {
        // articleData should contain { article: {...}, category: '...', country: '...' }
        return this.apiService.post(`/users/${userId}/saved-articles`, articleData);
    }

    unsaveArticle(userId: number, articleUrl: string): Observable<any> {
        return this.apiService.delete(`/users/${userId}/saved-articles`, { articleUrl });
    }

    getSavedArticles(userId: number): Observable<any[]> {
        return this.apiService.get(`/users/${userId}/saved-articles`);
    }

    getPersonalizedNews(userId: number): Observable<any> {
        return this.apiService.get(`/users/${userId}/personalized-news`);
    }
}
