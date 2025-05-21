import { useState, useEffect, useMemo } from 'react';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { ArrowUpDown, Download, Star, UserCircle, CalendarDays, FileText, Users, BookOpen, Building } from 'lucide-react';
import { Separator } from '@/components/ui/separator';
import { IconFileTypeJpg, IconFileTypePdf, IconFileTypeTxt, IconFileTypeZip } from '@tabler/icons-react';

// --- Mock Data Types ---
interface Faculty {
  id: string;
  name: string;
}

interface Course {
  id: string;
  name: string;
  facultyId: string;
}

interface Lecturer {
  id: string;
  name: string;
  courseIds: string[];
}

type ContentCategory = "PDF" | "IMAGE" | "ZIP" | "TEXT" | "VIDEO";

interface ContentItem {
  id: string;
  title: string;
  description: string;
  category: ContentCategory;
  facultyId: string;
  courseId: string;
  lecturerId?: string;
  uploadDate: string;
  uploader: string;
  rating: number; // 0-5
  filePath?: string; // Optional path for download
}

interface SortOption {
    value: string;
    label: string;
}

// --- Mock Data ---
const mockFaculties: Faculty[] = [
  { id: 'f1', name: 'Informatik' },
  { id: 'f2', name: 'Wirtschaftswissenschaften' },
  { id: 'f3', name: 'Ingenieurwesen' },
];

const mockCourses: Course[] = [
  { id: 'c1', name: 'Programmierung I', facultyId: 'f1' },
  { id: 'c2', name: 'Datenbanken', facultyId: 'f1' },
  { id: 'c3', name: 'Marketing Grundlagen', facultyId: 'f2' },
  { id: 'c4', name: 'Maschinenelemente', facultyId: 'f3' },
  { id: 'c5', name: 'Software Engineering', facultyId: 'f1'},
];

const mockLecturers: Lecturer[] = [
  { id: 'l1', name: 'Prof. Dr. Müller', courseIds: ['c1', 'c5'] },
  { id: 'l2', name: 'Dr. Schmidt', courseIds: ['c2'] },
  { id: 'l3', name: 'Prof. Weber', courseIds: ['c3'] },
  { id: 'l4', name: 'Dr. Klein', courseIds: ['c4'] },
];

const mockContentItems: ContentItem[] = [
  { id: 'item1', title: 'Java Grundlagen Skript', description: 'Umfassendes Skript zu den Grundlagen von Java.', category: 'PDF', facultyId: 'f1', courseId: 'c1', lecturerId: 'l1', uploadDate: '2024-05-10', uploader: 'MaxM', rating: 4.5, filePath: '/downloads/java_grundlagen.pdf' },
  { id: 'item2', title: 'ER-Diagramm Übung', description: 'Übungsaufgaben zur Erstellung von Entity-Relationship-Diagrammen.', category: 'IMAGE', facultyId: 'f1', courseId: 'c2', lecturerId: 'l2', uploadDate: '2024-05-12', uploader: 'LisaS', rating: 4.2, filePath: '/downloads/er_diagramm.png' },
  { id: 'item3', title: 'Marketing Mix Folien', description: 'Vorlesungsfolien zum Thema Marketing Mix.', category: 'PDF', facultyId: 'f2', courseId: 'c3', lecturerId: 'l3', uploadDate: '2024-04-20', uploader: 'AnnaW', rating: 3.8 },
  { id: 'item4', title: 'Festigkeitslehre Formelsammlung', description: 'Wichtige Formeln für Maschinenelemente.', category: 'PDF', facultyId: 'f3', courseId: 'c4', lecturerId: 'l4', uploadDate: '2024-03-15', uploader: 'TomK', rating: 4.0 },
  { id: 'item5', title: 'Projektmanagement Plan', description: 'Beispielhafter Projektmanagementplan für Softwareprojekte.', category: 'TEXT', facultyId: 'f1', courseId: 'c5', lecturerId: 'l1', uploadDate: '2024-05-01', uploader: 'MaxM', rating: 4.7 },
  { id: 'item6', title: 'SQL Cheat Sheet', description: 'Kurzübersicht der wichtigsten SQL-Befehle.', category: 'PDF', facultyId: 'f1', courseId: 'c2', lecturerId: 'l2', uploadDate: '2024-05-18', uploader: 'ChrisP', rating: 4.9, filePath: '/downloads/sql_cheatsheet.pdf' },
  { id: 'item7', title: 'Einführung Wirtschaft', description: 'Grundlagen der Wirtschaftswissenschaften.', category: 'VIDEO', facultyId: 'f2', courseId: 'c3', lecturerId: 'l3', uploadDate: '2024-05-20', uploader: 'LauraB', rating: 3.5 },
];

const contentCategories: ContentCategory[] = ["PDF", "IMAGE", "ZIP", "TEXT", "VIDEO"];

const sortOptions: SortOption[] = [
    { value: 'rating_desc', label: 'Beste Bewertung' },
    { value: 'date_desc', label: 'Neueste zuerst' },
    { value: 'date_asc', label: 'Älteste zuerst' },
    { value: 'title_asc', label: 'Titel (A-Z)' },
    { value: 'title_desc', label: 'Titel (Z-A)' },
];

// --- Helper function to get category icon ---
const GetCategoryIcon = ({ category, className }: { category: ContentCategory, className?: string }) => {
    const defaultClassName = "mr-2 h-4 w-4";
    const combinedClassName = `${defaultClassName} ${className || ''}`;
    switch (category) {
        case 'PDF': return <IconFileTypePdf/>;
        case 'IMAGE': return <IconFileTypeJpg/>; 
        case 'ZIP': return <IconFileTypeZip/>; 
        case 'TEXT': return <IconFileTypeTxt />;
        default: return <FileText className={combinedClassName} />;
    }
};


export function BrowsePage() {
  const [selectedFaculty, setSelectedFaculty] = useState<string | undefined>(undefined);
  const [selectedCourse, setSelectedCourse] = useState<string | undefined>(undefined);
  const [selectedLecturer, setSelectedLecturer] = useState<string | undefined>(undefined);
  const [selectedCategory, setSelectedCategory] = useState<ContentCategory | undefined>(undefined);
  const [currentSortBy, setCurrentSortBy] = useState<string>(sortOptions[0].value);

  const [availableCourses, setAvailableCourses] = useState<Course[]>([]);
  const [availableLecturers, setAvailableLecturers] = useState<Lecturer[]>([]);

  // Effect to update available courses when faculty changes
  useEffect(() => {
    if (selectedFaculty) {
      setAvailableCourses(mockCourses.filter(course => course.facultyId === selectedFaculty));
    } else {
      setAvailableCourses([]);
    }
    setSelectedCourse(undefined); // Reset course when faculty changes
    setSelectedLecturer(undefined); // Reset lecturer
  }, [selectedFaculty]);

  // Effect to update available lecturers when course changes
  useEffect(() => {
    if (selectedCourse) {
      const courseLecturerIds = mockLecturers
        .filter(lecturer => lecturer.courseIds.includes(selectedCourse))
        .map(l => l.id);
      setAvailableLecturers(mockLecturers.filter(lecturer => courseLecturerIds.includes(lecturer.id)));
    } else {
      setAvailableLecturers([]);
    }
    setSelectedLecturer(undefined); // Reset lecturer when course changes
  }, [selectedCourse]);

  const filteredAndSortedContent = useMemo(() => {
    let items = [...mockContentItems];

    if (selectedFaculty) {
      items = items.filter(item => item.facultyId === selectedFaculty);
    }
    if (selectedCourse) {
      items = items.filter(item => item.courseId === selectedCourse);
    }
    if (selectedLecturer) {
      items = items.filter(item => item.lecturerId === selectedLecturer);
    }
    if (selectedCategory) {
      items = items.filter(item => item.category === selectedCategory);
    }

    // Sorting logic
    switch (currentSortBy) {
        case 'rating_desc':
            items.sort((a, b) => b.rating - a.rating);
            break;
        case 'date_desc':
            items.sort((a, b) => new Date(b.uploadDate).getTime() - new Date(a.uploadDate).getTime());
            break;
        case 'date_asc':
            items.sort((a, b) => new Date(a.uploadDate).getTime() - new Date(b.uploadDate).getTime());
            break;
        case 'title_asc':
            items.sort((a, b) => a.title.localeCompare(b.title));
            break;
        case 'title_desc':
            items.sort((a, b) => b.title.localeCompare(a.title));
            break;
        default:
            break;
    }

    return items;
  }, [selectedFaculty, selectedCourse, selectedLecturer, selectedCategory, currentSortBy]);

  return (
    <div className="p-4 md:p-6 space-y-6">
      {/* Filter Section */}
      <div className="flex flex-col md:flex-row gap-4 items-center">
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-4 w-full">
          <Select value={selectedFaculty} onValueChange={setSelectedFaculty}>
            <SelectTrigger>
              <SelectValue placeholder="Fakultät wählen" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">Alle Fakultäten</SelectItem>
              {mockFaculties.map(faculty => (
                <SelectItem key={faculty.id} value={faculty.id}>{faculty.name}</SelectItem>
              ))}
            </SelectContent>
          </Select>

          <Select value={selectedCourse} onValueChange={setSelectedCourse} disabled={!selectedFaculty || selectedFaculty === 'all'}>
            <SelectTrigger>
              <SelectValue placeholder="Kurs wählen" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">Alle Kurse</SelectItem>
              {availableCourses.map(course => (
                <SelectItem key={course.id} value={course.id}>{course.name}</SelectItem>
              ))}
            </SelectContent>
          </Select>

          <Select value={selectedLecturer} onValueChange={setSelectedLecturer} disabled={!selectedCourse || selectedCourse === 'all'}>
            <SelectTrigger>
              <SelectValue placeholder="Dozent wählen" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">Alle Dozenten</SelectItem>
              {availableLecturers.map(lecturer => (
                <SelectItem key={lecturer.id} value={lecturer.id}>{lecturer.name}</SelectItem>
              ))}
            </SelectContent>
          </Select>

          <Select value={selectedCategory} onValueChange={(value) => setSelectedCategory(value === "all" ? undefined : value as ContentCategory)}>
            <SelectTrigger>
              <SelectValue placeholder="Inhaltstyp wählen" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">Alle Typen</SelectItem>
              {contentCategories.map(category => (
                <SelectItem key={category} value={category}>{category}</SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>

        <div className="md:ml-auto flex-shrink-0">
            <DropdownMenu>
                <DropdownMenuTrigger asChild>
                    <Button variant="outline">
                        <ArrowUpDown className="mr-2 h-4 w-4" />
                        Sortieren nach: {sortOptions.find(opt => opt.value === currentSortBy)?.label || 'Relevanz'}
                    </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end">
                    {sortOptions.map((option) => (
                        <DropdownMenuItem
                            key={option.value}
                            onClick={() => setCurrentSortBy(option.value)}
                        >
                            {option.label}
                        </DropdownMenuItem>
                    ))}
                </DropdownMenuContent>
            </DropdownMenu>
        </div>
      </div>

      <Separator />

      {/* Content Display Section */}
      {filteredAndSortedContent.length > 0 ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          {filteredAndSortedContent.map(item => (
            <Card key={item.id} className="flex flex-col">
              <CardHeader>
                <CardTitle className="text-lg line-clamp-2">{item.title}</CardTitle>
                <div className="flex items-center text-sm text-muted-foreground pt-1">
                    <GetCategoryIcon category={item.category} />
                    <span>{item.category}</span>
                    <span className="mx-1.5">·</span>
                    <CalendarDays className="mr-1.5 h-4 w-4" />
                    <span>{new Date(item.uploadDate).toLocaleDateString()}</span>
                </div>
              </CardHeader>
              <CardContent className="flex-grow">
                <p className="text-sm text-muted-foreground line-clamp-3">{item.description}</p>
              </CardContent>
              <CardFooter className="flex flex-col items-start space-y-2 pt-4 border-t">
                <div className="flex justify-between w-full text-xs text-muted-foreground">
                    <div className="flex items-center">
                        <UserCircle className="mr-1.5 h-4 w-4" />
                        <span>{item.uploader}</span>
                    </div>
                    <div className="flex items-center">
                        <Star className="mr-1 h-4 w-4 text-yellow-400 fill-yellow-400" />
                        <span>{item.rating.toFixed(1)}</span>
                    </div>
                </div>
                 <div className="w-full space-y-1 text-xs text-muted-foreground">
                    <div className="flex items-center">
                        <Building className="mr-1.5 h-3.5 w-3.5" />
                        <span>{mockFaculties.find(f => f.id === item.facultyId)?.name || 'N/A'}</span>
                    </div>
                    <div className="flex items-center">
                        <BookOpen className="mr-1.5 h-3.5 w-3.5" />
                        <span>{mockCourses.find(c => c.id === item.courseId)?.name || 'N/A'}</span>
                    </div>
                    {item.lecturerId && (
                         <div className="flex items-center">
                            <Users className="mr-1.5 h-3.5 w-3.5" />
                            <span>{mockLecturers.find(l => l.id === item.lecturerId)?.name || 'N/A'}</span>
                        </div>
                    )}
                </div>
                {item.filePath && (
                  <Button variant="outline" size="sm" className="w-full mt-2">
                    <Download className="mr-2 h-4 w-4" />
                    Herunterladen
                  </Button>
                )}
              </CardFooter>
            </Card>
          ))}
        </div>
      ) : (
        <div className="text-center py-10">
          <p className="text-xl font-semibold text-muted-foreground">Keine Inhalte gefunden.</p>
          <p className="text-sm text-muted-foreground">Versuche andere Filterkriterien.</p>
        </div>
      )}
    </div>
  );
}