import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from '../../../core/services/user.service';
import { AuthService } from '../../../core/services/auth.service';
import { ArticleCardComponent } from '../../../shared/components/article-card/article-card.component';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';

@Component({
    selector: 'app-saved-articles',
    standalone: true,
    imports: [CommonModule, ArticleCardComponent, LoadingSpinnerComponent],
    templateUrl: './saved-articles.component.html',
    styleUrl: './saved-articles.component.css'
})
export class SavedArticlesComponent implements OnInit {
    articles: any[] = [];
    loading: boolean = false;
    currentUser: any = null;

    constructor(
        private userService: UserService,
        private authService: AuthService
    ) { }

    ngOnInit() {
        this.authService.currentUser$.subscribe(user => {
            this.currentUser = user;
            if (user) {
                this.loadSavedArticles();
            }
        });
    }

    loadSavedArticles() {
        if (!this.currentUser) return;
        this.loading = true;
        this.userService.getSavedArticles(this.currentUser.id).subscribe({
            next: (articles) => {
                // The backend returns {url, category, country} objects, but ArticleCard expects standard NewsAPI format.
                // I need to adapt them or update ArticleCard to handle this format.
                // The backend `getSavedArticles` returns List<Map<String, String>> with url, category, country.
                // It DOES NOT return title, image, etc. This is a backend limitation identified in the summary.
                // "Saved articles are stored as strings... rather than a separate Article entity table."
                // This means I can't display the full article card unless I fetch the article details again from the News API.
                // But the News API doesn't have a "get article by URL" endpoint easily (maybe search by URL?).
                // Or I just display what I have (URL) which is poor UI.

                // Wait, the user wants a "way better" UI.
                // If the backend only stores the URL, I can't show the image/title unless I fetch it.
                // I can try to search for the URL in the News API? No, that's unreliable.
                // I should probably have stored the title/image in the backend.
                // But I am not supposed to change the backend unless necessary?
                // The user said "consuming the apis from the spring boot project".
                // If the backend is limited, the frontend will be limited.
                // HOWEVER, I can try to fetch the metadata from the URL using a scraper? No, too complex.

                // Let's look at `UserService.saveArticle`. It takes `articleUrl`, `category`, `country`.
                // It doesn't take title/image.
                // So the backend really only has the URL.

                // Workaround:
                // 1. Display a simple list of links (boring).
                // 2. Try to fetch the article content?

                // Let's assume for now I just display the URL and category.
                // Or maybe I can use the `url` as the title if title is missing.
                // I'll map the backend response to a structure `ArticleCardComponent` can accept, 
                // using the URL as the title and description.

                this.articles = articles.map(a => ({
                    title: a.url, // Fallback title
                    description: `Category: ${a.category} | Country: ${a.country}`,
                    url: a.url,
                    urlToImage: null, // No image
                    source: { name: 'Saved' },
                    publishedAt: null
                }));
                this.loading = false;
            },
            error: (err) => {
                console.error('Error loading saved articles', err);
                this.loading = false;
            }
        });
    }

    onUnsaveArticle(article: any) {
        if (!this.currentUser) return;
        this.userService.unsaveArticle(this.currentUser.id, article.url).subscribe(() => {
            this.articles = this.articles.filter(a => a.url !== article.url);
        });
    }
}
