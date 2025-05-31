import React, { useState, useEffect, useCallback } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { apiClient } from '@/api/apiClient';
import { toastSuccess, toastError } from '@/components/ui/sonner';
import { ContentCategory, Course, Faculty, Lecturer } from '@/lib/types';
import { getAllFaculties } from '@/api/facultyApi';
import { getAllCourses } from '@/api/courseApi';
import { getAllLecturers } from '@/api/lecturerApi';
import { UploadCloud } from 'lucide-react';
import { cn } from '@/lib/utils';

const contentCategories: ContentCategory[] = ["PDF", "IMAGE", "ZIP"];

export function ContentCreateForm() {
    const [title, setTitle] = useState('');
    const [file, setFile] = useState<File | null>(null);
    const [selectedFaculty, setSelectedFaculty] = useState<string | undefined>();
    const [selectedCourse, setSelectedCourse] = useState<string | undefined>();
    const [selectedLecturer, setSelectedLecturer] = useState<string | undefined>();
    const [selectedCategory, setSelectedCategory] = useState<ContentCategory | undefined>();
    const [isLoading, setIsLoading] = useState(false);
    const [isDragging, setIsDragging] = useState(false);

    const [faculties, setFaculties] = useState<Faculty[]>([]);
    const [courses, setCourses] = useState<Course[]>([]);
    const [lecturers, setLecturers] = useState<Lecturer[]>([]);

    useEffect(() => {
        getAllFaculties().then(setFaculties);
        getAllCourses().then(setCourses);
        getAllLecturers().then(setLecturers);
    }, []);

    const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        if (event.target.files && event.target.files[0]) {
            setFile(event.target.files[0]);
        }
    };

    const handleDrop = useCallback((event: React.DragEvent<HTMLDivElement>) => {
        event.preventDefault();
        event.stopPropagation();
        setIsDragging(false);
        if (event.dataTransfer.files && event.dataTransfer.files[0]) {
            setFile(event.dataTransfer.files[0]);
            toastSuccess({ title: "Datei ausgewählt", message: event.dataTransfer.files[0].name });
        }
    }, []);

    const handleDragOver = useCallback((event: React.DragEvent<HTMLDivElement>) => {
        event.preventDefault();
        event.stopPropagation();
        setIsDragging(true);
    }, []);

    const handleDragLeave = useCallback((event: React.DragEvent<HTMLDivElement>) => {
        event.preventDefault();
        event.stopPropagation();
        setIsDragging(false);
    }, []);

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
            lecturerId: selectedLecturer && selectedLecturer !== "none" ? parseInt(selectedLecturer) : null,
        };
        formData.append('contentData', new Blob([JSON.stringify(contentData)], { type: "application/json" }));

        try {
            const response = await apiClient.post('/api/contents', formData);
            toastSuccess({ title: 'Erfolg!', message: `"${response.data.title}" wurde erfolgreich hochgeladen.` });
            setTitle('');
            setFile(null);
            setSelectedFaculty(undefined);
            setSelectedCourse(undefined);
            setSelectedLecturer(undefined);
            setSelectedCategory(undefined);
        } catch (error: any) {
            console.error('Upload failed:', error);
            const errorMessage = error.response?.data?.message || error.message || "Upload fehlgeschlagen.";
            toastError({ title: 'Upload Fehler', message: errorMessage });
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <form onSubmit={handleSubmit} className="space-y-6 p-4 md:p-6 w-full">
            <div>
                <Label htmlFor="title" className="mb-1 inline-block">Titel</Label>
                <Input id="title" value={title} onChange={(e) => setTitle(e.target.value)} required disabled={isLoading} className="mt-1 w-full" />
            </div>
            <div>
                <Label htmlFor="file-upload" className="mb-1 inline-block">Datei auswählen</Label>
                <div
                    id="file-upload-area"
                    className={cn(
                        "mt-1 flex justify-center items-center px-6 pt-5 pb-6 border-2 border-dashed rounded-md cursor-pointer h-32 w-full",
                        isDragging ? "border-primary bg-primary/10" : "border-input hover:border-primary/70",
                        file ? "border-green-500 bg-green-500/10" : ""
                    )}
                    onDrop={handleDrop}
                    onDragOver={handleDragOver}
                    onDragLeave={handleDragLeave}
                    onClick={() => document.getElementById('file')?.click()}
                >
                    <div className="space-y-1 text-center">
                        <UploadCloud className={cn("mx-auto h-10 w-10", file ? "text-green-600" : "text-muted-foreground")} />
                        <div className="flex text-sm text-muted-foreground">
                            <span className={cn(file ? "text-green-700" : "")}>
                                {file ? file.name : "Datei hierher ziehen oder klicken"}
                            </span>
                            <Input id="file" type="file" className="sr-only" onChange={handleFileChange} disabled={isLoading} />
                        </div>
                        {!file && <p className="text-xs text-muted-foreground">PNG, JPG, GIF, PDF, ZIP bis zu 10MB</p>}
                    </div>
                </div>
            </div>

            <div>
                <Label htmlFor="faculty" className="mb-1 inline-block">Fakultät</Label>
                <Select onValueChange={setSelectedFaculty} value={selectedFaculty} disabled={isLoading}>
                    <SelectTrigger className="mt-1 w-full"><SelectValue placeholder="Fakultät wählen..." /></SelectTrigger>
                    <SelectContent>
                        {faculties.map(f => <SelectItem key={f.id} value={f.id.toString()}>{f.name}</SelectItem>)}
                    </SelectContent>
                </Select>
            </div>
            <div>
                <Label htmlFor="course" className="mb-1 inline-block">Kurs</Label>
                <Select onValueChange={setSelectedCourse} value={selectedCourse} disabled={isLoading || !selectedFaculty}>
                    <SelectTrigger className="mt-1 w-full"><SelectValue placeholder="Kurs wählen..." /></SelectTrigger>
                    <SelectContent>
                        {courses
                            .filter(c => selectedFaculty ? c.faculty.id === parseInt(selectedFaculty) : true)
                            .map(c => <SelectItem key={c.id} value={c.id.toString()}>{c.name}</SelectItem>)}
                    </SelectContent>
                </Select>
            </div>
            <div>
                <Label htmlFor="lecturer" className="mb-1 inline-block">Dozent</Label>
                <Select onValueChange={setSelectedLecturer} value={selectedLecturer} disabled={isLoading || !selectedCourse}>
                    <SelectTrigger className="mt-1 w-full"><SelectValue placeholder="Dozent wählen..." /></SelectTrigger>
                    <SelectContent>
                        {lecturers
                            .filter(l => selectedCourse ? l.courseIds.includes(parseInt(selectedCourse)) : true)
                            .map(l => <SelectItem key={l.id} value={l.id.toString()}>{l.name}</SelectItem>)}
                    </SelectContent>
                </Select>
            </div>
            <div>
                <Label htmlFor="category" className="mb-1 inline-block">Kategorie</Label>
                <Select onValueChange={(value) => setSelectedCategory(value as ContentCategory)} value={selectedCategory} disabled={isLoading}>
                    <SelectTrigger className="mt-1 w-full"><SelectValue placeholder="Kategorie wählen..." /></SelectTrigger>
                    <SelectContent>
                        {contentCategories.map(cat => <SelectItem key={cat} value={cat}>{cat}</SelectItem>)}
                    </SelectContent>
                </Select>
            </div>

            <Button type="submit" className="w-full mt-2" disabled={isLoading}>
                {isLoading ? 'Lädt hoch...' : 'Hochladen'}
            </Button>
        </form>
    );
}