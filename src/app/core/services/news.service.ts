import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

@Injectable({
    providedIn: 'root'
})
export class NewsService {

    constructor(private apiService: ApiService) { }

    getTopHeadlines(country: string = 'us'): Observable<any> {
        return this.apiService.get(`/news/top-headlines?country=${country}`);
    }

    searchNews(query: string): Observable<any> {
        return this.apiService.get(`/news/search?q=${query}`);
    }

    getNewsByCategory(category: string): Observable<any> {
        return this.apiService.get(`/news/category/${category}`);
    }
}
