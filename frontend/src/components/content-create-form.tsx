// frontend/src/components/upload-form.tsx (New File)
import React, { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { apiClient } from '@/api/apiClient'; // Your API client
import { toastSuccess, toastError } from '@/components/ui/sonner';
import { ContentCategory, Course, Faculty, Lecturer } from '@/lib/types'; //
import { getAllFaculties } from '@/api/facultyApi'; //
import { getAllCourses } from '@/api/courseApi'; //
import { getAllLecturers } from '@/api/lecturerApi'; //

const contentCategories: ContentCategory[] = ["PDF", "IMAGE", "ZIP"]; //

export function ContentCreateForm() {
    const [title, setTitle] = useState('');
    const [file, setFile] = useState<File | null>(null);
    const [selectedFaculty, setSelectedFaculty] = useState<string | undefined>();
    const [selectedCourse, setSelectedCourse] = useState<string | undefined>();
    const [selectedLecturer, setSelectedLecturer] = useState<string | undefined>();
    const [selectedCategory, setSelectedCategory] = useState<ContentCategory | undefined>();
    const [isLoading, setIsLoading] = useState(false);

    const [faculties, setFaculties] = useState<Faculty[]>([]);
    const [courses, setCourses] = useState<Course[]>([]);
    const [lecturers, setLecturers] = useState<Lecturer[]>([]);

    useEffect(() => {
        getAllFaculties().then(setFaculties);
        getAllCourses().then(setCourses); // You might want to filter these based on faculty
        getAllLecturers().then(setLecturers); // You might want to filter these based on course
    }, []);

    const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        if (event.target.files && event.target.files[0]) {
            setFile(event.target.files[0]);
        }
    };

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        if (!file || !selectedFaculty || !selectedCourse || !selectedCategory || !title) {
            toastError({ title: 'Fehler', message: 'Bitte alle Pflichtfelder ausfüllen und eine Datei auswählen.' });
            return;
        }
        setIsLoading(true);

        const formData = new FormData();
        formData.append('file', file);

        const contentData = {
            title: title,
            contentCategory: selectedCategory,
            courseId: parseInt(selectedCourse),
            facultyId: parseInt(selectedFaculty),
            lecturerId: selectedLecturer ? parseInt(selectedLecturer) : null,
        };
        // The backend @RequestPart("contentData") expects a JSON string or an object it can map.
        // If it expects a JSON string for "contentData":
        formData.append('contentData', new Blob([JSON.stringify(contentData)], { type: "application/json" }));
        // Or if your backend can map directly from individual form parts and you named them like `createRequest.title` etc.:
        // formData.append('title', title);
        // ... append other metadata fields ...


        try {
            // Make sure your apiClient is configured for multipart/form-data
            // Axios typically handles this automatically when you pass FormData
            const response = await apiClient.post('/api/contents', formData, {
                headers: {
                    // 'Content-Type': 'multipart/form-data' // Axios usually sets this for FormData
                },
            });
            toastSuccess({ title: 'Erfolg!', message: `"${response.data.title}" wurde erfolgreich hochgeladen.` });
            // Reset form or navigate
            setTitle('');
            setFile(null);
            // ... reset other fields
        } catch (error: any) {
            console.error('Upload failed:', error);
            const errorMessage = error.response?.data?.message || error.message || "Upload fehlgeschlagen.";
            toastError({ title: 'Upload Fehler', message: errorMessage });
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <form onSubmit={handleSubmit} className="space-y-6 p-4 md:p-6 max-w-lg mx-auto">
            <div>
                <Label htmlFor="title">Titel der Datei</Label>
                <Input id="title" value={title} onChange={(e) => setTitle(e.target.value)} required disabled={isLoading} />
            </div>
            <div>
                <Label htmlFor="file">Datei auswählen</Label>
                <Input id="file" type="file" onChange={handleFileChange} required disabled={isLoading} />
            </div>

            <div>
                <Label htmlFor="faculty">Fakultät</Label>
                <Select onValueChange={setSelectedFaculty} value={selectedFaculty} disabled={isLoading}>
                    <SelectTrigger><SelectValue placeholder="Fakultät wählen..." /></SelectTrigger>
                    <SelectContent>
                        {faculties.map(f => <SelectItem key={f.id} value={f.id.toString()}>{f.name}</SelectItem>)}
                    </SelectContent>
                </Select>
            </div>
             <div>
                <Label htmlFor="course">Kurs</Label>
                <Select onValueChange={setSelectedCourse} value={selectedCourse} disabled={isLoading || !selectedFaculty}>
                    <SelectTrigger><SelectValue placeholder="Kurs wählen..." /></SelectTrigger>
                    <SelectContent>
                        {courses
                            .filter(c => selectedFaculty ? c.faculty.id === parseInt(selectedFaculty) : true)
                            .map(c => <SelectItem key={c.id} value={c.id.toString()}>{c.name}</SelectItem>)}
                    </SelectContent>
                </Select>
            </div>
             <div>
                <Label htmlFor="lecturer">Dozent (optional)</Label>
                <Select onValueChange={setSelectedLecturer} value={selectedLecturer} disabled={isLoading || !selectedCourse}>
                    <SelectTrigger><SelectValue placeholder="Dozent wählen..." /></SelectTrigger>
                    <SelectContent>
                        <SelectItem value="none">Kein Dozent</SelectItem>
                        {lecturers
                            .filter(l => selectedCourse ? l.courseIds.includes(parseInt(selectedCourse)) : true)
                            .map(l => <SelectItem key={l.id} value={l.id.toString()}>{l.name}</SelectItem>)}
                    </SelectContent>
                </Select>
            </div>
            <div>
                <Label htmlFor="category">Kategorie</Label>
                <Select onValueChange={(value) => setSelectedCategory(value as ContentCategory)} value={selectedCategory} disabled={isLoading}>
                    <SelectTrigger><SelectValue placeholder="Kategorie wählen..." /></SelectTrigger>
                    <SelectContent>
                        {contentCategories.map(cat => <SelectItem key={cat} value={cat}>{cat}</SelectItem>)}
                    </SelectContent>
                </Select>
            </div>

            <Button type="submit" className="w-full" disabled={isLoading}>
                {isLoading ? 'Lädt hoch...' : 'Hochladen'}
            </Button>
        </form>
    );
}