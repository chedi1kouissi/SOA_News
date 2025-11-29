import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { NewsService } from '../../../core/services/news.service';
import { UserService } from '../../../core/services/user.service';
import { AuthService } from '../../../core/services/auth.service';
import { ArticleCardComponent } from '../../../shared/components/article-card/article-card.component';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';

@Component({
    selector: 'app-home',
    standalone: true,
    imports: [CommonModule, FormsModule, ArticleCardComponent, LoadingSpinnerComponent],
    templateUrl: './home.component.html',
    styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
    articles: any[] = [];
    loading: boolean = false;
    currentCategory: string = 'general';
    currentCountry: string = 'us';
    searchQuery: string = '';

    categories = ['general', 'business', 'technology', 'sports', 'entertainment', 'health', 'science'];
    countries = [
        { code: 'us', name: 'USA' },
        { code: 'gb', name: 'UK' },
        { code: 'fr', name: 'France' },
        { code: 'de', name: 'Germany' },
        { code: 'in', name: 'India' }
    ];

    currentUser: any = null;
    savedArticleUrls: Set<string> = new Set();

    constructor(
        private newsService: NewsService,
        private userService: UserService,
        private authService: AuthService,
        private router: Router
    ) { }

    ngOnInit() {
        this.authService.currentUser$.subscribe(user => {
            this.currentUser = user;
            if (user) {
                this.loadUserPreferences();
            }
        });
        this.loadHeadlines();
    }

    loadHeadlines() {
        this.loading = true;
        this.newsService.getTopHeadlines(this.currentCountry).subscribe({
            next: (data) => {
                this.articles = data.articles || [];
                this.loading = false;
            },
            error: (err) => {
                console.error('Error loading news', err);
                this.loading = false;
            }
        });
    }

    loadByCategory(category: string) {
        this.currentCategory = category;
        this.loading = true;
        if (category === 'general') {
            this.loadHeadlines();
        } else {
            this.newsService.getNewsByCategory(category).subscribe({
                next: (data) => {
                    this.articles = data.articles || [];
                    this.loading = false;
                },
                error: (err) => {
                    console.error('Error loading category', err);
                    this.loading = false;
                }
            });
        }
    }

    onCountryChange(countryCode: string) {
        this.currentCountry = countryCode;
        this.loadHeadlines();
    }

    onSearch() {
        if (this.searchQuery.trim()) {
            this.router.navigate(['/search'], { queryParams: { q: this.searchQuery } });
        }
    }

    loadUserPreferences() {
        if (!this.currentUser) return;
        this.userService.getSavedArticles(this.currentUser.id).subscribe({
            next: (articles) => {
                this.savedArticleUrls = new Set(articles.map(a => a.url));
            },
            error: (err) => console.error('Error loading preferences', err)
        });
    }

    onSaveArticle(article: any) {
        if (!this.currentUser) {
            alert('Please login to save articles');
            return;
        }
        this.userService.saveArticle(this.currentUser.id, {
            article: article,
            category: this.currentCategory,
            country: this.currentCountry
        }).subscribe(() => {
            this.savedArticleUrls.add(article.url);
            alert('Article saved!');
        });
    }

    onUnsaveArticle(article: any) {
        if (!this.currentUser) return;

        this.userService.unsaveArticle(this.currentUser.id, article.url).subscribe(() => {
            this.savedArticleUrls.delete(article.url);
        });
    }

    isArticleSaved(article: any): boolean {
        return this.savedArticleUrls.has(article.url);
    }
}
