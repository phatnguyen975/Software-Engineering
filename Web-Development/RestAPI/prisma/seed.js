import { prisma } from "../src/configs/prisma.js";

async function main() {
  console.log("🌱 Seeding courses...");

  const sampleCourses = [];

  const instructors = [
    "Nguyễn Văn A",
    "Trần Thị B",
    "Lê Hoàng C",
    "Phạm Duy D",
    "Võ Minh E",
  ];

  const courseNames = [
    "JavaScript Cơ Bản",
    "ReactJS Từ Zero đến Hero",
    "Node.js Backend Pro",
    "Docker & DevOps Foundations",
    "Kubernetes Thực Chiến",
    "Python AI Cơ Bản",
    "Python Web với Django",
    "Machine Learning Cơ Bản",
    "SQL & Database Design",
    "HTML/CSS Masterclass",
    "TailwindCSS Full Course",
    "Next.js Advanced",
    "Java Spring Boot API",
    "C# .NET Backend",
    "Android Kotlin App Dev",
    "iOS SwiftUI Pro",
    "Git & GitHub Mastery",
    "UI/UX Figma Design",
    "TypeScript Masterclass",
    "Cloud Computing AWS"
  ];

  for (let i = 0; i < 20; i++) {
    sampleCourses.push({
      name: courseNames[i],
      description: `Khóa học ${courseNames[i]} dành cho người mới bắt đầu.`,
      courseImage: `https://picsum.photos/seed/course_${i}/600/400`,
      instructor: instructors[i % instructors.length],
      price: Number((Math.random() * 100 + 50).toFixed(2)),
      rating: Number((Math.random() * 5).toFixed(1)),
    });
  }

  await prisma.course.createMany({
    data: sampleCourses,
  });

  console.log("✅ Seed completed!");
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
