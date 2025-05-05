const apiBase = '/api';

async function fetchMovies() {
    try {
        const response = await fetch(`${apiBase}/movies`);
        if (!response.ok) {
            console.error('Failed to fetch movies');
            alert('Error fetching movies');
            return;
        }
        const movies = await response.json();

        const list = document.getElementById('movie-list');
        list.innerHTML = '';

        for (const movie of movies) {
            const li = document.createElement('li');
            li.innerHTML = `<strong>${movie.title}</strong> (ID: ${movie.id})
                <button onclick="deleteMovie(${movie.id})">Delete</button>
                <ul id="actors-${movie.id}"></ul>`;
            list.appendChild(li);

            await fetchActors(movie.id);
        }
    } catch (error) {
        console.error('Error fetching movies:', error);
        alert('Error fetching movies');
    }
}

async function fetchActors(movieId) {
    try {
        const response = await fetch(`${apiBase}/actors/movie/${movieId}`);
        if (!response.ok) {
            console.error('Failed to fetch actors');
            alert('Error fetching actors');
            return;
        }
        const actors = await response.json();
        const ul = document.getElementById(`actors-${movieId}`);
        ul.innerHTML = '';

        for (const actor of actors) {
            const li = document.createElement('li');
            li.textContent = `${actor.name} (ID: ${actor.id})`;
            li.innerHTML += ` <button onclick="deleteActor(${actor.id})">Delete</button>`;
            ul.appendChild(li);
        }
    } catch (error) {
        console.error(`Error fetching actors for movie ${movieId}:`, error);
        alert('Error fetching actors');
    }
}

async function addMovie() {
    const title = document.getElementById('movie-title').value;
    if (!title) return alert("Movie title is required");

    try {
        const response = await fetch(`${apiBase}/movies`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ title })
        });

        if (!response.ok) {
            console.error('Failed to add movie');
            alert('Error adding movie');
            return;
        }

        document.getElementById('movie-title').value = '';
        await fetchMovies();
    } catch (error) {
        console.error('Error adding movie:', error);
        alert('Error adding movie');
    }
}

async function addActor() {
    const name = document.getElementById('actor-name').value;
    const movieId = document.getElementById('actor-movie-id').value;

    if (!name || !movieId) return alert("Actor name and movie ID are required");

    try {
        const response = await fetch(`${apiBase}/actors/movie/${movieId}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name })
        });

        if (!response.ok) {
            console.error('Failed to add actor');
            alert('Error adding actor');
            return;
        }

        document.getElementById('actor-name').value = '';
        document.getElementById('actor-movie-id').value = '';
        await fetchMovies();
    } catch (error) {
        console.error('Error adding actor:', error);
        alert('Error adding actor');
    }
}

async function deleteMovie(id) {
    try {
        const response = await fetch(`${apiBase}/movies/${id}`, { method: 'DELETE' });
        if (!response.ok) {
            console.error('Failed to delete movie');
            alert('Error deleting movie');
            return;
        }
        await fetchMovies();
    } catch (error) {
        console.error('Error deleting movie:', error);
        alert('Error deleting movie');
    }
}

async function deleteActor(id) {
    try {
        const response = await fetch(`${apiBase}/actors/${id}`, { method: 'DELETE' });
        if (!response.ok) {
            console.error('Failed to delete actor');
            alert('Error deleting actor');
            return;
        }
        await fetchMovies();
    } catch (error) {
        console.error('Error deleting actor:', error);
        alert('Error deleting actor');
    }
}
